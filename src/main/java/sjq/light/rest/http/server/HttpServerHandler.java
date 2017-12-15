package sjq.light.rest.http.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AsciiString;
import sjq.light.rest.http.exception.BadRequestException;
import sjq.light.rest.http.request.Request;
import sjq.light.rest.http.response.Response;
import sjq.light.rest.http.rest.MatchAction;
import sjq.light.rest.http.rest.RestHandler;
import sjq.light.rest.util.ExceptionUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);

    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    private RestDispatcher restDispatcher;

    public HttpServerHandler(RestDispatcher restDispatcher) {
        super();
        this.restDispatcher = restDispatcher;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        if (HttpUtil.is100ContinueExpected(httpRequest)) {
            ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
        }

        boolean needCloseConnection = needCloseConnection(httpRequest);
        FullHttpResponse httpResponse = restHandler(httpRequest);

        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        if (!keepAlive) {
            LOGGER.debug("close connection.");
            ctx.write(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            LOGGER.debug("keep connection.");
            // 继续保持连接。
            httpResponse.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(httpResponse);
        }

        if (needCloseConnection) {
        		ctx.flush();
        		ctx.close();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private boolean needCloseConnection(HttpRequest httpRequest) {
        boolean connectionClose = false;
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
            // http head
            HttpHeaders headers = fullHttpRequest.headers();
            for (Entry<String, String> entry : headers.entries()) {
                if (entry.getKey().equalsIgnoreCase("connection")) {
                    if (entry.getValue().equalsIgnoreCase("close")) {
                        connectionClose = true;
                        break;
                    }
                }
            }
        }

        return connectionClose;
    }

    private FullHttpResponse restHandler(HttpRequest httpRequest) {
        String method = httpRequest.method().toString().toUpperCase();
        String uri = httpRequest.uri();

        int splitIndex = uri.indexOf("?");
        String preUri;
        if (splitIndex > 0) {
            preUri = uri.substring(0, splitIndex);
        } else {
            preUri = uri;
        }

        MatchAction matchAction = restDispatcher.findBasicRESTHandler(preUri);
        if (matchAction == null) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            return response;
        }

        String httpContent = null;
        Map<String, String> headMap = null;
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;

            // http head
            HttpHeaders headers = fullHttpRequest.headers();
            if (headers.size() > 0) {
                headMap = new HashMap<>();
            }

            for (Entry<String, String> entry : headers.entries()) {
                headMap.put(entry.getKey(), entry.getValue());
            }

            // http body
            ByteBuf contentByteBuf = fullHttpRequest.content();
            String contentEncoding = headMap.get("Content-Encoding");
            if (contentEncoding != null && contentEncoding.equalsIgnoreCase("gzip")) {
                byte[] bytes = ByteBufUtil.getBytes(contentByteBuf);
                byte[] decompressBytes = null;

                // GZIP decompress
                if (bytes == null || bytes.length == 0) {
                    return null;
                }
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                try {
                    GZIPInputStream ungzip = new GZIPInputStream(in);
                    byte[] buffer = new byte[256];
                    int n;
                    while ((n = ungzip.read(buffer)) >= 0) {
                        out.write(buffer, 0, n);
                    }
                    decompressBytes = out.toByteArray();
                    httpContent = new String(decompressBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                httpContent = contentByteBuf.toString(Charset.defaultCharset());
            }
        }

        Map<String, String> pathParams = matchAction.getMatchParams().getParams();
        Map<String, List<String>> queryParams = paraseQueryParams(uri);
        RestHandler restHandler = matchAction.getRestHandler();
        Request request = new Request(uri, headMap, pathParams, queryParams, httpContent);

        Response httpResponse = null;
        try {
            switch (method) {
            case "GET":
                httpResponse = restHandler.get(request);
                break;
            case "POST":
                httpResponse = restHandler.post(request);
                break;
            case "PUT":
                httpResponse = restHandler.put(request);
                break;
            case "DELETE":
                httpResponse = restHandler.delete(request);
                break;
            case "HEAD":
                httpResponse = restHandler.head(request);
                break;
            case "OPTIONS":
                httpResponse = restHandler.options(request);
                break;
            default:
                LOGGER.warn("can't support http method :{}", method);
                FullHttpResponse HttpResponse400 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NOT_FOUND);
                return HttpResponse400;
            }

            String responseContent = httpResponse.getBodyContent();
            
            if (responseContent == null || responseContent.length() == 0) {
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.NO_CONTENT);
                return response;
            }
            
            httpResponse.setStatus(200);
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(responseContent.getBytes(Charset.defaultCharset()))
            );
            
            HttpHeaders headers = fullHttpResponse.headers();
            for (Entry<String, String> entry : httpResponse.getHeadMap().entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
            headers.setInt(CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
            
            return fullHttpResponse;
        } catch (BadRequestException badRequestException) {
            LOGGER.error(ExceptionUtils.parseExceptionStackTrace(badRequestException));
            FullHttpResponse fullHttpResponse = null;
            if (badRequestException.getStatus() == 400) {
                fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            } else {
                fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                        HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }

            return fullHttpResponse;
        } catch (Exception exception) {
            LOGGER.error(ExceptionUtils.parseExceptionStackTrace(exception));
            FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                    HttpResponseStatus.INTERNAL_SERVER_ERROR);
            return fullHttpResponse;
        }
    }

    private Map<String, List<String>> paraseQueryParams(String uri) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        return parameters;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("Registered");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("Unregistered");
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("Active");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("Inactive");
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//        LOGGER.info("EventTriggered");
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
//        LOGGER.info("WritabilityChanged");
        super.channelWritabilityChanged(ctx);
    }

}

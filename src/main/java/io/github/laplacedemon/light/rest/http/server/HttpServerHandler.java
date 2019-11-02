package io.github.laplacedemon.light.rest.http.server;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.light.rest.http.connection.ChannelAttribute;
import io.github.laplacedemon.light.rest.http.connection.IOSession;
import io.github.laplacedemon.light.rest.http.exception.BadRequestException;
import io.github.laplacedemon.light.rest.http.request.RestRequest;
import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.github.laplacedemon.light.rest.http.rest.MatchAction;
import io.github.laplacedemon.light.rest.http.rest.RestHandler;
import io.github.laplacedemon.light.rest.util.ExceptionUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerHandler.class);
    
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
    	
        restHandler(httpRequest, ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void restHandler(final HttpRequest httpRequest, final ChannelHandlerContext ctx) {
    	IOSession ioSession = ChannelAttribute.getIOSession(ctx);
    	
        String method = httpRequest.method().toString().toUpperCase();
        String uri = httpRequest.uri();
        
        final int splitIndex = uri.indexOf("?");
        String preUri;
        if (splitIndex > 0) {
            preUri = uri.substring(0, splitIndex);
        } else {
            preUri = uri;
        }
        
        final MatchAction matchAction = restDispatcher.findBasicRESTHandler(preUri);
        if (matchAction == null) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            ctx.channel().writeAndFlush(response);
            return ;
        }
        
        final boolean willCloseConnection = checkWillCloseConnection(httpRequest);
        if (willCloseConnection) {
        	ioSession.willClose();
        }
        
        Map<String, String> pathParams = matchAction.getMatchParams().getParams();
        Map<String, List<String>> queryParams = paraseQueryParams(uri);
        RestHandler restHandler = matchAction.getRestHandler();
        RestRequest request = new RestRequest(uri, pathParams, queryParams, httpRequest);
        try {
            switch (method) {
            case "GET":
                restHandler.get(request, ioSession);
                break;
            case "POST":
                restHandler.post(request, ioSession);
                break;
            case "PUT":
                restHandler.put(request, ioSession);
                break;
            case "PATCH":
            	restHandler.patch(request, ioSession);
                break;
            case "DELETE":
                restHandler.delete(request, ioSession);
                break;
            case "HEAD":
                restHandler.head(request, ioSession);
                break;
            case "OPTIONS":
                restHandler.options(request, ioSession);
                break;
            default:
                LOGGER.warn("can't support http method :{}", method);
                RestResponse restResponse = new RestResponse();
                restResponse.setStatus(HttpResponseStatus.NOT_FOUND.code());
                ioSession.writeAndFlush(restResponse);
            }
            
            return ;
        } catch (BadRequestException badRequestException) {
            LOGGER.error(ExceptionUtils.parseExceptionStackTrace(badRequestException));
            
            RestResponse restResponse = new RestResponse();
            if (badRequestException.getStatus() == 400) {
                restResponse.setStatus(HttpResponseStatus.BAD_REQUEST.code());
            } else {
                restResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
            }
            
            ioSession.writeAndFlush(restResponse);
            return ;
        } catch (Exception exception) {
    		LOGGER.error(ExceptionUtils.parseExceptionStackTrace(exception));
    		RestResponse restResponse = new RestResponse();
    		restResponse.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
    		restResponse.setBodyContent(exception.getMessage());
            ioSession.writeAndFlush(restResponse);
            return ;
        }
    }

    private Map<String, List<String>> paraseQueryParams(String uri) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        return parameters;
    }
    
    private boolean checkWillCloseConnection(final HttpRequest httpRequest) {
	    if (httpRequest instanceof FullHttpRequest) {
			boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
			if(!keepAlive) {
				return keepAlive;
			}
	    }
	    
        return false;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        ChannelAttribute.initSession(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

}

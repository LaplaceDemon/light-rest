package io.github.laplacedemon.light.rest.http.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.light.rest.util.StringUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.CharsetUtil;

public class StaticFileHandler extends SimpleChannelInboundHandler<HttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticFileHandler.class);
    private final String uriPrefix;
    private final String staticResourcesPath;

    public StaticFileHandler(String uriPrefix, String staticResourcesPath) {
        this.uriPrefix = uriPrefix;
        this.staticResourcesPath = staticResourcesPath;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) throws Exception {
        final boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        String uri = httpRequest.uri();
        if (uri.startsWith(uriPrefix)) {
            String resourceName = uri.substring(uriPrefix.length());
            
            final ChannelFuture flushFuture;
            if (StringUtils.isEmpty(this.staticResourcesPath)) {
                InputStream inputStream = StaticFileHandler.class.getClassLoader().getResourceAsStream("static/" + resourceName);
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                byte[] buf = new byte[50];
                while (true) {
                    int read = inputStream.read(buf);
                    if (read <= 0) {
                        break;
                    }
                    response.content().writeBytes(buf, 0, read);
                }

                flushFuture = ctx.writeAndFlush(response);
            } else {
                String fileName = this.staticResourcesPath + "/" + resourceName;
                File file = new File(fileName);
                if (!file.isFile()) {
                    sendError(ctx, HttpResponseStatus.NOT_FOUND);
                }

                final int fileLength = (int) file.length();
                final RandomAccessFile raf;
                try {
                    raf = new RandomAccessFile(file, "r");
                } catch (Exception ex) {
                    sendError(ctx, HttpResponseStatus.NOT_FOUND);
                    return;
                }

                final FileRegion fileRegion = new DefaultFileRegion(raf.getChannel(), 0, fileLength);

                // write head
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                HttpUtil.setContentLength(response, fileLength);
                setContentTypeHeader(response, file);
                ctx.write(response);

                // write file
                ChannelFuture sendFileFuture = ctx.write(fileRegion, ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override

                    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
                        if (total < 0) { // total unknown
                            LOGGER.info(future.channel() + " Transfer progress: " + progress);
                        } else {
                            LOGGER.info(future.channel() + " Transfer progress: " + progress + " / " + total);
                        }
                    }

                    @Override
                    public void operationComplete(ChannelProgressiveFuture future) {
                        LOGGER.info(future.channel() + " Transfer complete.");
                        try {
                            raf.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // write file
                flushFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            }

            if (keepAlive) {
                flushFuture.addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            sendError(ctx, HttpResponseStatus.NOT_FOUND, "Can't find the resources.");
        }
    }

    private static void setContentTypeHeader(HttpResponse response, File file) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimeTypesMap.getContentType(file.getPath()));
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String text) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + text + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}

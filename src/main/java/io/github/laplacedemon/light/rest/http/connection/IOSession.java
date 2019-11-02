package io.github.laplacedemon.light.rest.http.connection;

import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.light.rest.http.response.RestResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AsciiString;

public class IOSession {
	private final static AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
	private final static AsciiString CONNECTION = new AsciiString("Connection");
	private final static AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

	private final static Logger LOGGER = LoggerFactory.getLogger(IOSession.class);

	private final Channel channel;
	private final AtomicBoolean requestWillCloseConnection;

	public IOSession(final Channel channel) {
		this.channel = channel;
		this.requestWillCloseConnection = new AtomicBoolean(false);
	}
	
	public void writeAndFlush(final RestResponse restHttpResponse) {
		this.writeAndFlush(restHttpResponse, null);
	}

	public void writeAndFlush(final RestResponse restHttpResponse, final Runnable runnable) {
		final FullHttpResponse nettyFillHttpResponse;
		
		String responseContent = restHttpResponse.getBodyContent();
		int statusCode = restHttpResponse.getStatus();
		if (statusCode == 204 && (responseContent == null || responseContent.length() == 0)) {
			nettyFillHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT);
		} else {
			HttpResponseStatus httpResponseStatus = HttpResponseStatus.valueOf(statusCode);
			if(statusCode >= 400) {
				LOGGER.error( "Http status code:" + httpResponseStatus.codeAsText().toString() + ", Content:" + restHttpResponse.getBodyContent());
			}
			
			nettyFillHttpResponse = new DefaultFullHttpResponse(
				HttpVersion.HTTP_1_1,
				httpResponseStatus,
				Unpooled.wrappedBuffer(responseContent.getBytes(Charset.defaultCharset()))
			);

			HttpHeaders headers = nettyFillHttpResponse.headers();
			
			for (Entry<String, String> entry : restHttpResponse.getHeadMap().entrySet()) {
				headers.set(entry.getKey(), entry.getValue());
			}

			headers.setInt(CONTENT_LENGTH, nettyFillHttpResponse.content().readableBytes());
		}
		
		boolean responseWillCloseConnection = checkResponseWillCloseConnection(nettyFillHttpResponse);
		if (responseWillCloseConnection || this.requestWillCloseConnection.get()) {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("close connection.");
			}
			nettyFillHttpResponse.headers().set(CONNECTION, "close");
			ChannelFuture channelFuture = channel.writeAndFlush(nettyFillHttpResponse);
			
			if(runnable != null) {
				channelFuture = channelFuture.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						runnable.run();
					}
					
				});
			}
			
			channelFuture.addListener(ChannelFutureListener.CLOSE);
			return ;
		} else {
			if(LOGGER.isDebugEnabled()) {
				LOGGER.debug("keep connection.");
			}
			nettyFillHttpResponse.headers().set(CONNECTION, KEEP_ALIVE);
			ChannelFuture channelFuture = this.channel.writeAndFlush(nettyFillHttpResponse);
			if(runnable != null) {
				channelFuture.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future) throws Exception {
						runnable.run();
					}
					
				});
			}
			return ;
		}
	}

	private boolean checkResponseWillCloseConnection(final HttpResponse httpResponse) {
		if (httpResponse.status().code() >= 300) {
			return true;
		}

		return false;
	}

	public void willClose() {
		this.requestWillCloseConnection.set(true);
	}

}

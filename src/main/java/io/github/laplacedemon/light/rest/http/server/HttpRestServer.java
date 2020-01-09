package io.github.laplacedemon.light.rest.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.laplacedemon.light.rest.http.rest.RestHandler;
import io.github.laplacedemon.light.rest.http.url.URLParser;
import io.github.laplacedemon.light.rest.ioc.IoCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class HttpRestServer implements AutoCloseable {
	private static Logger LOGGER = LoggerFactory.getLogger(HttpRestServer.class);
	private int port;
	private IoCFactory iocFactory;
	private final ServerBootstrap bootstrap;
	private final EventLoopGroup bossGroup;
	private final EventLoopGroup workerGroup;
	private RestDispatcher restDispatcher;
	private final EventExecutorGroup eventExecutorGroup;

	public HttpRestServer(final int port) {
		this(port, null);
	}

	public HttpRestServer(final int port, final String uploadFilePath) {
		this(port, uploadFilePath, null);
	}

	public HttpRestServer(final int port, final String uploadFilePath, final String staticIndex) {
		this.port = port;
		this.iocFactory = new IoCFactory();
		this.bossGroup = new NioEventLoopGroup(1);
		this.workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
		this.eventExecutorGroup = new DefaultEventExecutorGroup(16);

		this.bootstrap = new ServerBootstrap();
		this.bootstrap.group(bossGroup, workerGroup)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.SO_REUSEADDR, true)
				.channel(NioServerSocketChannel.class)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 64 * 1024))
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("codec", new HttpServerCodec())
								.addLast("aggregator", new HttpObjectAggregator(1048576))
								.addLast(eventExecutorGroup, "rest", new HttpServerHandler(restDispatcher))
								.addLast(eventExecutorGroup, "file", new HttpStaticFileServerHandler(uploadFilePath, staticIndex));
					}
				});
	}

	public void scanRestPackage(final String packageName) {
		this.restDispatcher = RestDispatcher.createDispatcher(packageName, this.iocFactory);
	}

	public void register(final String urlTemplate, final RestHandler basicRESTHandler) {
		URLParser urlParse = URLParser.parse(urlTemplate);
		restDispatcher.register(urlParse, basicRESTHandler);
	}

	public IoCFactory iocFactory() {
		return iocFactory;
	}

	public void start() {
		try {
			Channel ch = bootstrap.bind(port).sync().channel();
			LOGGER.info("the light-rest has been started");
			ch.closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}

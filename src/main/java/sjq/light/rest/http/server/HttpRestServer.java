package sjq.light.rest.http.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private ServerBootstrap bootstrap;
	private EventLoopGroup bossGroup;
	private EventLoopGroup workerGroup;
	private RestDispatcher restDispatcher;
	private EventExecutorGroup eventExecutorGroup;
	// private String filePath;

	public HttpRestServer(int port) {
		this(port, null);
	}

	public HttpRestServer(int port, String filePath) {
		this.port = port;
		bossGroup = new NioEventLoopGroup(1);
		workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2);
		eventExecutorGroup = new DefaultEventExecutorGroup(16);

		bootstrap = new ServerBootstrap();
		final String _filePath = filePath;
		bootstrap.option(ChannelOption.SO_BACKLOG, 1024).option(ChannelOption.SO_REUSEADDR, true)
				.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(8 * 1024, 64 * 1024))
				.childOption(ChannelOption.TCP_NODELAY, true).childHandler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception {
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("codec", new HttpServerCodec())
								.addLast("aggregator", new HttpObjectAggregator(1048576))
								.addLast(eventExecutorGroup, "rest", new HttpServerHandler(restDispatcher))
								.addLast(eventExecutorGroup, "file", new HttpStaticFileServerHandler(_filePath));
					}
				});
	}

	public void scanRestPackage(String packageName) {
		restDispatcher = RestDispatcher.createDispatcher(packageName);
	}

	// public void setFilePath(String path) {
	// this.filePath = path;
	// }

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

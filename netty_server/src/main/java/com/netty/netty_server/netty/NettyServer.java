package com.netty.netty_server.netty;

import com.common.com.NettyMessageDecoder;
import com.common.com.NettyMessageEncoder;
import com.common.com.ServerHeartBeatSimpleHandle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <p>
 * 使用Netty创建Server程序.
 * <p>
 * Tips:
 * <ol>
 * <li>Netty默认的线程数量为CPU个数*2；</li>
 * <li>pipeline添加Handler时，可以通过参数指定执行Handler的线程池，默认为workerGroup执行；</li>
 * <li>LoggingHandler可以很方便观察到Netty方法执行过程，日志级别设置为DEBUG，log4j配置文件中也需要设置为debug；</li>
 * <li>注意Pipeline中事件处理，如果需要后续Handler接着处理，一定不要忘记调用ctx.fireXXX方法；</li>
 * </ol>
 * 
 */

public class NettyServer {
	private static Logger logger = LoggerFactory.getLogger(NettyServer.class);

	//CPU个数*2
	private static final int DEFAULT_THREAD_NUM = Runtime.getRuntime().availableProcessors() * 2;

	private int port = 8989;

	private int nioThreadNum = DEFAULT_THREAD_NUM;

	private int bizThreadNum = DEFAULT_THREAD_NUM;

	private ChannelFuture future;

	private ServerBootstrap bootstrap;

	private EventExecutorGroup bizGroup = null;

	public NettyServer(int port) {
		this.port = port;
	}

	public NettyServer(int port, int nioThreadNum, int bizThreadNum) {
		this.port = port;
		this.nioThreadNum = nioThreadNum;
		this.bizThreadNum = bizThreadNum;
	}

	public void start() {
		//NioEventLoopGroup 是用来处理I/O操作的多线程事件循环器
		// 第一个经常被叫做‘boss’，用来接收进来的连接。第二个经常被叫做‘worker’，用来处理已经被接收的连接， 一旦‘boss’接收到连接，就会把连接信息注册到‘worker’上。
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(nioThreadNum); // netty默认NIO线程数为CPU数*2

		// 业务线程池，用于执行业务
		bizGroup = new DefaultEventExecutorGroup(bizThreadNum);
		try {
			//ServerBootstrap 是一个启动NIO服务的辅助启动类 你可以在这个服务中直接使用Channel
			ServerBootstrap b = new ServerBootstrap();

			//设置连接模式nio
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
			// 设置用于ServerSocketChannel的属性和handler
			b.handler(new ChannelInitializer<ServerSocketChannel>() {
				protected void initChannel(ServerSocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
				}
			});
			b.option(ChannelOption.SO_BACKLOG, 128);
			b.option(ChannelOption.SO_REUSEADDR, true);

			// 设置用于SocketChannel的属性和handler
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
					//心跳监控
					ch.pipeline().addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
					//消息编码
					ch.pipeline().addLast(new NettyMessageEncoder());
					//消息解码
					ch.pipeline().addLast(new NettyMessageDecoder());
					//业务消息处理
					ch.pipeline().addLast(new ServerHeartBeatSimpleHandle(new ServerBusinessProcessor()));
				}
			});

			b.childOption(ChannelOption.SO_KEEPALIVE, true);
			b.childOption(ChannelOption.TCP_NODELAY, true);
			b.childOption(ChannelOption.SO_REUSEADDR, true);

			// Bind and start to accept incoming connections.
			future = b.bind(port).sync();
			bootstrap = b;
			logger.info("server started sucessfully.");
		} catch (Throwable t) {
			logger.error("异常", t);
		}
	}

	public void close() {
		// 关闭业务线程池
		if (bizGroup != null) {
			bizGroup.shutdownGracefully();
		}

		if (future != null) {
			future.channel().close();
		}

		if (bootstrap != null) {
			if (bootstrap.config().group() != null) {
				bootstrap.config().group().shutdownGracefully();
			}
			if (bootstrap.config().childGroup() != null) {
				bootstrap.config().childGroup().shutdownGracefully();
			}
		}
	}

	public int getPort() {
		return port;
	}

	public static void main(String[] args) {
		NettyServer server = new NettyServer(8989);
		server.start();
		try {
			waitToQuit(server);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void waitToQuit(NettyServer server) throws IOException {
		System.out.println(11111111);
		logger.info("Server is running on port {}, press q to quit.", server.getPort());
		boolean input = true;
		while (input) {
			int b = System.in.read();
			switch (b) {
			case 'q':
				logger.info("Server关闭...");
				server.close();
				input = false;
				break;
			case '\r':
			case '\n':
				break;
			default:
				logger.info("q -- quit.");
				break;
			}
		}
	}
}

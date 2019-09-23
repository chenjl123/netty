package com.netty.netty_client1.netty;

import java.util.concurrent.TimeUnit;

import com.common.com.ClientHeartBeatSimpleHandle;
import com.common.com.NettyMessageEncoder;
import com.common.com.discard.NettyBusinessDuplexHandler;
import com.common.com.NettyMessage;
import com.common.com.NettyMessageDecoder;
import com.common.com.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.stereotype.Service;

/**
 * NettyClient
 */

@Service
public class NettyClient {
	private static Logger logger = LoggerFactory.getLogger(NettyClient.class);
	
	Bootstrap bootstrap;

	public ChannelFuture future;

	private EventExecutorGroup bizGroup = null;

	public NettyClient() {
		bizGroup = new DefaultEventExecutorGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(1);
		try {
			Bootstrap b = new Bootstrap();
			b.group(workerGroup);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.SO_KEEPALIVE, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
					ch.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
					ch.pipeline().addLast(new NettyMessageEncoder());
					ch.pipeline().addLast(new NettyMessageDecoder());
					ch.pipeline().addLast(new ClientHeartBeatSimpleHandle(new ClientBusinessProcessor()));
				}
			});

			bootstrap = b;
			this.init();
		} catch (Throwable t) {
			logger.error("异常", t);
		}
	}

	/**
	 * 连接netty 服务器，注册
	 */
	private void init(){
		// 连接netty 服务器
		this.connect("127.0.0.1", 8989);

		//注册
		NettyMessage bizMsg = new NettyMessage("你好，我是client1");
        bizMsg.setMessageType(NettyMessage.MESSAGE_TYPE_REG);
        bizMsg.setLogId(Constants.CLIENT1);
        this.future.channel().writeAndFlush(bizMsg);
	}

	/**
	 * 连接远程主机
	 */
	public void connect(String ip, int port) {
		try {
			future = bootstrap.connect(ip, port).sync();
			logger.info("成功连接到 {}:{}", ip, port);
		} catch (InterruptedException e) {
			logger.info("连接服务器异常", e);
		}
	}

	public void disConnect() {
		if (future != null) {
			future.channel().close();
			future = null;
		}
	}

	/**
	 * 断开跟远程主机的连接，并关闭相应的线程等资源
	 */
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
		}

		logger.info("成功关闭");
	}

	public static void main(String[] args) {
		NettyClient client = new NettyClient();
		client.connect("127.0.0.1", 8989);

		NettyMessage bizMsg = new NettyMessage("ces");
		bizMsg.setLogId(1111111111);
		logger.info("发送消息  -- {}", bizMsg.toString());
		client.future.channel().writeAndFlush(bizMsg);
	}
}

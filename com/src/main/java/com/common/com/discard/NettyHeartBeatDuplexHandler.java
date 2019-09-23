package com.common.com.discard;

import com.common.com.ChannelMap;
import com.common.com.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * <p>
 * 心跳Handler,本示例中,心跳采用Ping-Ping模式,即服务端跟客户端互相发送,接收方收到直接丢弃,无需响应.
 * <p>
 * 心跳检测规则：如果20秒未发送任何内容，则发送心跳；如果60秒未收到任何内容，则认为对方超时，关闭连接；规则对双方都适用。
 */
public class NettyHeartBeatDuplexHandler extends  ChannelDuplexHandler {

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接关闭" + ctx.channel());
		super.channelInactive(ctx);
		//移除map的通道信息
		ChannelMap.remove(ctx);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("连接建立" + ctx.channel());
		super.channelActive(ctx);
		//ChannelMap.add("", ctx);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleState state = ((IdleStateEvent) evt).state();
			if (state == IdleState.WRITER_IDLE) { // 20s
				System.out.println("idle 20s, send heartbeat");
				ctx.writeAndFlush(null);
			} else if (state == IdleState.READER_IDLE) { // 60s
				System.out.println("连接timeout,请求关闭 " + ctx.channel());
				ctx.close();
				//移除map的通道信息
				ChannelMap.remove(ctx);
			}
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("异常: " + cause.getMessage());
		ctx.close();
	}
}

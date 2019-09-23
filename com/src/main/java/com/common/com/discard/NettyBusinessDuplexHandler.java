package com.common.com.discard;
import com.common.com.AppBusinessProcessor;
import com.common.com.ChannelMap;
import com.common.com.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 * 负责业务处理的Handler，运行在业务线程组<code>DefaultEventExecutorGroup</code>中，以免任务太耗时而导致NIO线程阻塞；
 *
 */
public class NettyBusinessDuplexHandler extends ChannelDuplexHandler {

	private AppBusinessProcessor bizProcessor = null;

	public NettyBusinessDuplexHandler(AppBusinessProcessor appBizHandler) {
		super();
		this.bizProcessor = appBizHandler;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage bizMsg = (NettyMessage) msg; // 拆分好的消息
		String opFrom = bizMsg.getFlag()==0?"上旬":"下旬";

		if (bizMsg.getMessageType() == NettyMessage.MESSAGE_TYPE_HB) {
			System.out.println(opFrom+"收到心跳  -- {}" + bizMsg.toString());
		} else if(NettyMessage.MESSAGE_TYPE_REG ==  bizMsg.getMessageType()) {
			System.out.println("客服端："+bizMsg.getLogId() +"注册到服务");
			//客服端注册,添加通道map
			ChannelMap.add(bizMsg.getLogId()+"", ctx);
		} else {
			// 处理业务消息
			//System.out.println(opFrom+"收到消息" + bizMsg.toString());
			bizProcessor.process(bizMsg);
		}
	}
}

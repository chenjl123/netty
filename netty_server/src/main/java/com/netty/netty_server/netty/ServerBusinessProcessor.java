package com.netty.netty_server.netty;

import com.common.com.AppBusinessProcessor;
import com.common.com.ChannelMap;
import com.common.com.NettyMessage;
import com.common.com.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * <p>
 * 服务端业务逻辑处理
 *
 */
public class ServerBusinessProcessor extends AppBusinessProcessor {
	
	public void process(NettyMessage message) {
		System.out.println("服务端接受到消息..." + message.getMessageBody());

		message.setMessageBody("{code:0, msg:操作成功}");
		message.setFlag((byte) 1);
		ChannelMap.get(message.getLogId()+"").writeAndFlush(message);
	}
}

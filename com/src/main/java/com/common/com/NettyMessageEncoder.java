package com.common.com;

import com.common.com.util.ByteTransUtil;
import com.common.com.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * <p>
 * 传送消息编码，将NettyMessage对象转换为字节数组
 *
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

	@Override
	protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
		//消息体
		String bodyMsg = msg.getMessageBody();
		//消息体长度
		int bodyLength = 0;

		if (bodyMsg != null) {
			byte[] messageBody = msg.getMessageBody().getBytes(Constants.ENCODING);
			bodyLength = messageBody.length;
			msg.setLength(bodyLength);

			//消息头 + 消息体长度
			byte[] data = new byte[NettyMessage.HEAD_LEN + bodyLength];
			System.arraycopy(ByteTransUtil.intToByteArray(msg.getMagicNumber(), false), 0, data, 0, 4);
			System.arraycopy(ByteTransUtil.intToByteArray(msg.getLength(), false), 0, data, 4, 4);
			System.arraycopy(ByteTransUtil.intToByteArray(msg.getMessageType(), false), 0, data, 8, 4);
			System.arraycopy(ByteTransUtil.intToByteArray(msg.getLogId(), false), 0, data, 12, 4);
			data[NettyMessage.HEAD_LEN - 1] = msg.getFlag();

			System.arraycopy(messageBody, 0, data, NettyMessage.HEAD_LEN, msg.getLength());

			//netty 缓冲
			ByteBuf buf = Unpooled.copiedBuffer(data);
			out.add(buf);
		} else {
			return;
		}
	}
}

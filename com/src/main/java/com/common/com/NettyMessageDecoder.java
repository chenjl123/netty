package com.common.com;

import com.common.com.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * <p>
 * 解码，将字节数组转换为NettyMessage对象
 *
 */
public class NettyMessageDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// 不够报文头长度,返回
		if (in.readableBytes() < NettyMessage.HEAD_LEN) {
			return;
		}
		
		in.markReaderIndex();
		int magicNumber = in.readInt();
		int length = in.readInt();
		int messageType = in.readInt();
		int logId = in.readInt();
		byte flag = in.readByte();
		
		// 如果magicNumber对不上或者length为负数，那有可能是通过telnet随意输入的内容，直接丢弃处理，不需要重置readerIndex
		if (magicNumber != Constants.MAGIC_NUMBER || length < 0) {
			System.out.println("非法输入,丢弃");
			return;
		}
		
		// 长度超过消息头长度,但是剩下的不够一个完整的报文,那么就重置readerIndex，返回等读取更多的数据再处理
		if (in.readableBytes() < length) {
			in.resetReaderIndex();
			return;
		}
		
		NettyMessage message = new NettyMessage(messageType, logId);
		message.setFlag(flag);
		byte[] bodyArray = new byte[length];
		in.readBytes(bodyArray);
		String body = new String(bodyArray, Constants.ENCODING);
		message.setMessageBody(body);
		out.add(message);
	}
}

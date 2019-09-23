package com.common.com.discard;

import com.common.com.util.ByteTransUtil;
import com.common.com.util.Constants;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

/**
 * 消息对象，根据协议定义消息格式。消息格式为：magicNumber+length+messageType+logId+flag+消息体byte[]，
 * 前16个字节分别对应4个整数字段，按大端存取数字,第17个字节通过0|1表示请求|响应；
 * 
 */
public class NettyMessageBak implements Serializable {
	private static final long serialVersionUID = 3201210212398130551L;

	/** 消息头字节长度 */
	public static final int HEAD_LEN = 17;

	/** 业务报文 */
	public static final int MESSAGE_TYPE_BIZ = 1;

	/** 心跳报文 */
	public static final int MESSAGE_TYPE_HB = 2;

	/** 注册报文 */
	public static final int MESSAGE_TYPE_REG = 3;

	/** 魔幻数，约定一个特定数字，所以以此值开头的报文才是有效报文  */
	private int magicNumber = Constants.MAGIC_NUMBER;

	/** 消息体长度，即messageBody.length */
	private int length = 0;

	/** 消息类型：1：业务消息；2：心跳消息 */
	private int messageType = MESSAGE_TYPE_BIZ;

	/** 请求方身份唯一标识，服务端通过唯一标示向客服端发送请求*/
	private int logId = 0;

	/** 0标示客户端向服务端上旬访问，1标示服务端向客服端下旬访问 */
	private byte flag = 0;

	/** 消息头，16字节长度，依次由四个数字组成：magicNumber|length|messageType|logId，数字按大端存取 */
	private byte[] messageHead;

	/** 消息体，默认为UTF-8编码 */
	private byte[] messageBody;

	/** 默认心跳报文 */
	public static final NettyMessageBak HEATBEAT_MSG = buildHeartBeatMsg();

	/**
	 * 生成心跳消息
	 * @return
	 */
	public static NettyMessageBak buildHeartBeatMsg() {
		NettyMessageBak hb = new NettyMessageBak();
		hb.setMessageType(MESSAGE_TYPE_HB);
		hb.setLogId(Constants.HB); // 心跳报文logId默认设置为10000000
		hb.setMessageBody("HB".getBytes()); // 默认编码即可,英文字符在所有编码结果都是一样的
		return hb;
	}

	public NettyMessageBak() { }

	public NettyMessageBak(int magicNumber, int length, int messageType, int logId) {
		super();
		this.magicNumber = magicNumber;
		this.length = length;
		this.messageType = messageType;
		this.logId = logId;
	}

	public NettyMessageBak(String msg) {
		if (msg == null || msg.length() == 0) {
			return;
		}
		try {
			this.messageBody = msg.getBytes(Constants.ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// 如果支持设置的编码格式，则采用系统默认的编码
			this.messageBody = msg.getBytes();
		}
		this.length = this.messageBody.length;
	}

	public NettyMessageBak(byte[] fullMsg) {
		if (fullMsg == null || fullMsg.length < HEAD_LEN) {
			return;
		}

		this.messageHead = new byte[HEAD_LEN];
		System.arraycopy(fullMsg, 0, messageHead, 0, HEAD_LEN);
		this.parseHead();
		if (fullMsg.length > HEAD_LEN) {
			this.messageBody = new byte[this.length];
			System.arraycopy(fullMsg, HEAD_LEN, this.messageBody, 0, this.length);
		}
	}

	public int getMagicNumber() {
		return magicNumber;
	}

	public void setMagicNumber(int magicNumber) {
		this.magicNumber = magicNumber;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public byte getFlag() {
		return flag;
	}

	public void setFlag(byte flag) {
		this.flag = flag;
	}

	public byte[] getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(byte[] messageBody) {
		this.messageBody = messageBody;
		if (this.messageBody != null) {
			this.length = this.messageBody.length;
		}
	}

	public void setMessageBody(String mb) {
		if (mb == null || mb.length() == 0) {
			return;
		}
		try {
			this.messageBody = mb.getBytes(Constants.ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.messageBody = mb.getBytes();
		}
		this.length = this.messageBody.length;
	}

	public byte[] getMessageHead() {
		if (this.messageHead == null) {
			this.composeHead();
		}
		return messageHead;
	}

	public void setMessageHead(byte[] messageHead) {
		this.messageHead = messageHead;
		this.parseHead();
	}

	private void parseHead() {
		if (messageHead == null || messageHead.length != HEAD_LEN) {
			return;
		}
		byte[] tmps = new byte[4];
		System.arraycopy(messageHead, 0, tmps, 0, 4);
		this.magicNumber = ByteTransUtil.byteArrayToInt(tmps, false);

		System.arraycopy(messageHead, 4, tmps, 0, 4);
		this.length = ByteTransUtil.byteArrayToInt(tmps, false);

		System.arraycopy(messageHead, 8, tmps, 0, 4);
		this.messageType = ByteTransUtil.byteArrayToInt(tmps, false);

		System.arraycopy(messageHead, 12, tmps, 0, 4);
		this.logId = ByteTransUtil.byteArrayToInt(tmps, false);
	}

	private void composeHead() {
		this.messageHead = new byte[HEAD_LEN];
		System.arraycopy(ByteTransUtil.intToByteArray(this.magicNumber, false), 0, messageHead, 0, 4);
		System.arraycopy(ByteTransUtil.intToByteArray(this.length, false), 0, messageHead, 4, 4);
		System.arraycopy(ByteTransUtil.intToByteArray(this.messageType, false), 0, messageHead, 8, 4);
		System.arraycopy(ByteTransUtil.intToByteArray(this.logId, false), 0, messageHead, 12, 4);
		this.messageHead[HEAD_LEN - 1] = flag;
	}

	@Override
	public String toString() {
		return MessageFormat.format(
				"Msg[magicNumber={0,number,###},length={1,number,###},messageType={2,number,###},logId={3,number,###},flag={4,number,###}][{5}]",
				new Object[] { magicNumber, length, messageType, logId, flag, bodyToString() });
	}

	public String bodyToString() {
		String body = null;
		if (this.messageBody != null && this.messageBody.length > 0) {
			try {
				body = new String(messageBody, Constants.ENCODING);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				body = new String(messageBody);
			}
		}
		return body;
	}

	/**
	 * 生成完整消息对应的字节数组，如果没有消息体，就只有头部
	 * 
	 * @return
	 */
	public byte[] composeFull(int flag) {
		this.flag = (byte) flag;

		if (this.messageBody != null) {
			this.length = this.messageBody.length;
		}

		byte[] data = new byte[this.length + HEAD_LEN];
		System.arraycopy(ByteTransUtil.intToByteArray(this.magicNumber, false), 0, data, 0, 4);
		System.arraycopy(ByteTransUtil.intToByteArray(this.length, false), 0, data, 4, 4);
		System.arraycopy(ByteTransUtil.intToByteArray(this.messageType, false), 0, data, 8, 4);
		System.arraycopy(ByteTransUtil.intToByteArray(this.logId, false), 0, data, 12, 4);
		data[HEAD_LEN - 1] = (byte) flag;
		if (this.messageBody != null) {
			System.arraycopy(this.messageBody, 0, data, HEAD_LEN, this.length);
		}
		return data;
	}
}

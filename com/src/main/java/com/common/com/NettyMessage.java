package com.common.com;

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
public class NettyMessage implements Serializable {
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
	private String messageBody;

	/** 默认心跳报文 */
	public static final NettyMessage HEATBEAT_MSG = buildHeartBeatMsg();

	/**
	 * 生成心跳消息
	 * @return
	 */
	public static NettyMessage buildHeartBeatMsg() {
		NettyMessage hb = new NettyMessage();
		hb.setMessageType(MESSAGE_TYPE_HB);
		hb.setLogId(Constants.HB); // 心跳报文logId默认设置为10000000
		hb.setMessageBody("HB"); // 默认编码即可,英文字符在所有编码结果都是一样的
		return hb;
	}

	public NettyMessage() { }

	public NettyMessage(int messageType, int logId) {
		this.messageType = messageType;
		this.logId = logId;
	}

	public NettyMessage(String msg) {
		if (msg == null || msg.length() == 0) {
			return;
		}
		this.messageBody = msg;
	}

	public int getMagicNumber() {
		return magicNumber;
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

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String mb) {
		if (mb == null || mb.length() == 0) {
			return;
		}
		this.messageBody = mb;
	}


}

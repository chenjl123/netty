package com.common.com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 业务逻辑处理抽象基类,服务端跟客户端分别实现自己的处理逻辑
 *
 */
public abstract class AppBusinessProcessor {
	protected static Logger logger = LoggerFactory.getLogger(AppBusinessProcessor.class);

	/**
	 *
	 * @param message
	 * 请求/响应消息载体
	 * 
	 */
	public abstract void process(NettyMessage message);
}

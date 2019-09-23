package com.netty.netty_client2.netty;

import com.common.com.AppBusinessProcessor;
import com.common.com.NettyMessage;

/**
 * <p>
 * 客户服务端业务逻辑处理
 */
public class ClientBusinessProcessor extends AppBusinessProcessor {

    public void process(NettyMessage message) {
        System.out.println("接受服务端响应:" + message.getMessageBody());
        //返回客户端请求参数，nettyCLient需要单例模式
    }
}

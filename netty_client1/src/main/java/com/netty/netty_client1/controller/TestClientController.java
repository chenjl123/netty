package com.netty.netty_client1.controller;

import com.common.com.NettyMessage;
import com.common.com.util.Constants;
import com.netty.netty_client1.netty.NettyClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestClientController {

    Logger logger = LoggerFactory.getLogger(TestClientController.class);

    @Autowired
    private NettyClient nettyClient;

    @GetMapping("/hello")
    public String hello(){
        NettyMessage bizMsg = new NettyMessage(NettyMessage.MESSAGE_TYPE_BIZ, Constants.CLIENT1);
        bizMsg.setMessageBody("请求封存记录");
        //此方法判断通道是否已经被服务端关闭
        logger.info("通道是否活着：" + nettyClient.future.channel().isActive());
        nettyClient.future.channel().writeAndFlush(bizMsg);
        return "success";
    }
}

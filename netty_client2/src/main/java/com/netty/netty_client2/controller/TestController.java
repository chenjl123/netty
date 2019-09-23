package com.netty.netty_client2.controller;

import com.common.com.NettyMessage;
import com.common.com.util.Constants;
import com.netty.netty_client2.netty.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private NettyClient nettyClient;

    @GetMapping("/hello")
    public String hello(){
        NettyMessage bizMsg = new NettyMessage("请求屏蔽002");
        bizMsg.setMessageType(NettyMessage.MESSAGE_TYPE_BIZ);
        bizMsg.setLogId(Constants.CLIENT2);
        nettyClient.future.channel().writeAndFlush(bizMsg);
        return "success";
    }
}

package com.netty.netty_server.controller;

import com.common.com.ChannelMap;
import com.common.com.NettyMessage;
import com.common.com.util.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/hello")
    public String hello(){
        NettyMessage bizMsg = new NettyMessage("请求用户列表");
        bizMsg.setMessageType(NettyMessage.MESSAGE_TYPE_BIZ);
        bizMsg.setLogId(Constants.CLIENT1);
        ChannelMap.get(Constants.CLIENT1+"").writeAndFlush(bizMsg);
        return "hello";
    }

    @GetMapping("/hello2")
    public String hello2(){
        NettyMessage bizMsg = new NettyMessage("请求用户列表");
        bizMsg.setMessageType(NettyMessage.MESSAGE_TYPE_BIZ);
        bizMsg.setLogId(Constants.CLIENT2);
        ChannelMap.get(Constants.CLIENT2+"").writeAndFlush(bizMsg);
        return "hello";
    }
}

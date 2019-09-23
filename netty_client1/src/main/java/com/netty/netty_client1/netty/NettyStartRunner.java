//package com.netty.netty_client1.netty;
//
//import com.common.com.NettyMessage;
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.core.annotation.Order;
//import org.springframework.stereotype.Component;
//
//@Component
//@Order(value = 2)
///**
// * 启动连接、注册netty 服务
// *
// */
//public class NettyStartRunner implements CommandLineRunner {
//    @Override
//    public void run(String... args) throws Exception {
//        //连接netty服务器
//        NettyClient client = new NettyClient();
//        client.connect("127.0.0.1", 8989);
//
//        //注册
//        NettyMessage bizMsg = new NettyMessage("你好，我是client1");
//        bizMsg.setMessageType(NettyMessage.MESSAGE_TYPE_REG);
//        bizMsg.setLogId(1111111111);
//        ByteBuf buf = Unpooled.copiedBuffer(bizMsg.composeFull());
//        client.future.channel().writeAndFlush(buf);
//    }
//}

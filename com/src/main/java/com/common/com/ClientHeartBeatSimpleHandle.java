package com.common.com;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * 客服端心跳检测
 */
public class ClientHeartBeatSimpleHandle extends SimpleChannelInboundHandler<Object> {
    private AppBusinessProcessor bizProcessor = null;

    public ClientHeartBeatSimpleHandle(AppBusinessProcessor appBizHandler) {
        super();
        this.bizProcessor = appBizHandler;
    }
    /**
     * 客服端接受到消息会触发该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        //System.out.println("客服端接收到服务端消息内容：" + msg.toString(CharsetUtil.UTF_8));
        NettyMessage bizMsg = (NettyMessage) msg;
        if (bizMsg.getMessageType() == NettyMessage.MESSAGE_TYPE_HB) {
            System.out.println("收到服务端心跳" + bizMsg.toString());
        } else {
            // 处理业务消息
            //System.out.println(opFrom+"收到消息" + bizMsg.toString());
            bizProcessor.process(bizMsg);
        }
    }

    /**
     * 设置超时会调用此方法
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                //10 空闲就向服务端发送一个ping心跳包
                ctx.writeAndFlush(NettyMessage.HEATBEAT_MSG);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * 服务端关闭客服端连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
       super.channelInactive(ctx);
       System.out.println("服务端关闭了客服端");
       //断线重新连接服务器
    }
}

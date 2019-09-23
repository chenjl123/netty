package com.common.com;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;

/**
 * 服务端心跳检测
 */
public class ServerHeartBeatSimpleHandle extends SimpleChannelInboundHandler<Object> {
    private AppBusinessProcessor bizProcessor = null;

    public ServerHeartBeatSimpleHandle(AppBusinessProcessor appBizHandler) {
        super();
        this.bizProcessor = appBizHandler;
    }

    /**
     * 服务端接受到消息会触发该方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage bizMsg = (NettyMessage) msg; // 拆分好的消息
        if (bizMsg.getMessageType() == NettyMessage.MESSAGE_TYPE_HB) {
            System.out.println("收到心跳客服端:" + bizMsg.getLogId() + "心跳");

            //收到客服端心跳，回复一个pong给客服端
            //ctx.writeAndFlush(bizMsg);
        } else if(NettyMessage.MESSAGE_TYPE_REG ==  bizMsg.getMessageType()) {
            System.out.println("客服端：" + bizMsg.getLogId() + "注册到服务");
            //客服端注册,添加通道map
            ChannelMap.add(bizMsg.getLogId()+"", ctx);
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
            if (state == IdleState.READER_IDLE) {
                //30秒没有读，关闭客服端连接，可以做个超时次数限制
                System.out.println("客服端发送心跳超时，断开连接");
                ctx.close();
                //移除map的通道信息
                ChannelMap.remove(ctx);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    /**
     * 客服端连接到服务器
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.println("连接建立:" + ctx.channel());
    }

    /**
     * 客服端断开连接
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        //服务端map，移除对应的连接通道
        ChannelMap.remove(ctx);
    }
}

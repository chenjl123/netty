package com.common.com;

import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端用于存放客服端通道map
 */
public class ChannelMap {
    private static Map<String, ChannelHandlerContext> ctxMap = new ConcurrentHashMap<String, ChannelHandlerContext>();

    /**
     * 客户端注册请求时候，添加通道到map
     * @param client_id
     * @param ctx
     */
    public static void add(String client_id, ChannelHandlerContext ctx) {
        ctxMap.put(client_id, ctx);
    }

    /**
     * 服务端通过通道ID获取对象，向客户端推送消息
     * @param client_id
     * @return
     */
    public static ChannelHandlerContext get(String client_id) {
        return ctxMap.get(client_id);
    }

    /**
     * 客服端断开连接，清除通道
     * @param ctx
     */
    public static void remove(ChannelHandlerContext ctx) {
        for (Map.Entry entry:ctxMap.entrySet()) {
            if (entry.getValue() == ctx) {
                String client_id = (String) entry.getKey();
                ctxMap.remove(client_id);
            }
        }
    }

}

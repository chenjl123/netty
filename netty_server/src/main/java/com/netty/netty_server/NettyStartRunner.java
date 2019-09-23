package com.netty.netty_server;

import com.netty.netty_server.netty.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
/**
 * 启动netty 服务
 *
 */
public class NettyStartRunner implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        NettyServer server = new NettyServer(8989);
        server.start();
    }
}

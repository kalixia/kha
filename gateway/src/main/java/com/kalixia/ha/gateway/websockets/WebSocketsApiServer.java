package com.kalixia.ha.gateway.websockets;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketsApiServer {
    private ServerBootstrap wsServerBootstrap;
    private final int port;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketsApiServer.class);

    public WebSocketsApiServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        wsServerBootstrap = new ServerBootstrap();
        try {
            wsServerBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new WebSocketServerInitializer());

            Channel ch = wsServerBootstrap.bind().sync().channel();
            LOGGER.info("WebSockets API available at port {}.", port);

            ch.closeFuture().sync();
        } finally {
            wsServerBootstrap.shutdown();
        }
    }

    public void stop() {
        wsServerBootstrap.shutdown();
    }

}

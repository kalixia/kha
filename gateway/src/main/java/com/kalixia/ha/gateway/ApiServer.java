package com.kalixia.ha.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiServer {
    private ServerBootstrap apiBootstrap;
    private final int port;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServer.class);

    public ApiServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        apiBootstrap = new ServerBootstrap();
        try {
            apiBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ApiServerChannelInitializer());

            Channel ch = apiBootstrap.bind().sync().channel();
            LOGGER.info("WebSockets API available at port {}.", port);

            ch.closeFuture().sync();
        } finally {
            apiBootstrap.shutdown();
        }
    }

    public void stop() {
        apiBootstrap.shutdown();
    }

}

package com.kalixia.ha.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioServerSocketChannel;
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
            // the gateway will only have a few connections, so OIO is likely to be faster than NIO in this case!
            apiBootstrap.group(new OioEventLoopGroup(), new OioEventLoopGroup())
                    .channel(OioServerSocketChannel.class)
                    .localAddress(port)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ApiServerChannelInitializer());

            ChannelFuture f = apiBootstrap.bind().sync();
            LOGGER.info("WebSockets API available on port {}.", port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Can't start API server", e);
        } finally {
            apiBootstrap.shutdown();
        }
    }

    public void stop() {
        apiBootstrap.shutdown();
    }

}

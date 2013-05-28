package com.kalixia.ha.gateway;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

public class ApiServer {
    private ServerBootstrap apiBootstrap;
    private final int port;
    private OioEventLoopGroup parentGroup;
    private OioEventLoopGroup childGroup;
    private final ChannelHandler channelInitializer;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServer.class);

    public ApiServer(int port, ChannelHandler channelInitializer) {
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    public void start() {
        apiBootstrap = new ServerBootstrap();
        parentGroup = new OioEventLoopGroup();
        childGroup = new OioEventLoopGroup();
        try {
            // the gateway will only have a few connections, so OIO is likely to be faster than NIO in this case!
            apiBootstrap.group(parentGroup, childGroup)
                    .channel(OioServerSocketChannel.class)
                    .localAddress(port)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(channelInitializer);

            ChannelFuture f = apiBootstrap.bind().sync();
            LOGGER.info("WebSockets API available on port {}.", port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Can't start API server", e);
        }
    }

    public void stop() throws InterruptedException {
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        parentGroup.awaitTermination(1, TimeUnit.MINUTES);
        childGroup.awaitTermination(1, TimeUnit.MINUTES);
    }

}

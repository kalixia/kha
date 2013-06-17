package com.kalixia.ha.cloud;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ApiServer {
    private ServerBootstrap apiBootstrap;
    private final int port;
    private final ChannelHandler channelInitializer;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiServer.class);

    public ApiServer(int port, ChannelHandler channelInitializer) {
        this.port = port;
        this.channelInitializer = channelInitializer;
    }

    public void start() {
        apiBootstrap = new ServerBootstrap();
        try {
            // the cloudPlatform will only have a few connections, so OIO is likely to be faster than NIO in this case!
            apiBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(channelInitializer);

            apiBootstrap.bind();
//            ChannelFuture f = apiBootstrap.bind().sync();
            LOGGER.info("WebSockets API available on port {}.", port);
//            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Can't start API server", e);
        }
    }

    public void stop() throws InterruptedException {
        EventLoopGroup bossGroup = apiBootstrap.group();
        EventLoopGroup workersGroup = apiBootstrap.childGroup();

        bossGroup.shutdownGracefully();
        workersGroup.shutdownGracefully();
        bossGroup.awaitTermination(1, TimeUnit.MINUTES);
        workersGroup.awaitTermination(1, TimeUnit.MINUTES);
    }

}

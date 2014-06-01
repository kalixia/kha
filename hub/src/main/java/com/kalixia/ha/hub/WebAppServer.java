package com.kalixia.ha.hub;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.lucene.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class WebAppServer {
    private ServerBootstrap apiBootstrap;
    private final int port;
    private final String hubSiteDirectory;
    private OioEventLoopGroup parentGroup;
    private OioEventLoopGroup childGroup;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAppServer.class);

    public WebAppServer(int port) {
        this.port = port;
        String hubHomeDirectory = System.getProperty("app.home");
        if (hubHomeDirectory.isEmpty()) {
            this.hubSiteDirectory = "src/main/webapp";
        } else {
            this.hubSiteDirectory = hubHomeDirectory + "/site";
        }
        LOGGER.debug("Website content served from '{}'", hubSiteDirectory);
    }

    public void start() {
        apiBootstrap = new ServerBootstrap();
        ThreadFactory threadFactory = new NamedThreadFactory("kha-webapp");
        EventLoopGroup commonGroup = new OioEventLoopGroup(0, threadFactory);
        try {
            // the hub will only have a few connections, so OIO is likely to be faster than NIO in this case!
            apiBootstrap.group(commonGroup, commonGroup)
                    .channel(OioServerSocketChannel.class)
                    .localAddress(port)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast("http-request-decoder", new HttpRequestDecoder());
//                            pipeline.addLast("deflater", new HttpContentDecompressor());
                            pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(1048576));
                            pipeline.addLast("http-response-encoder", new HttpResponseEncoder());
//                            pipeline.addLast("inflater", new HttpContentCompressor());
                            pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                            pipeline.addLast("file-handler", new HttpStaticFileServerHandler(hubSiteDirectory, true));
                        }
                    });

            ChannelFuture f = apiBootstrap.bind().sync();
            LOGGER.info("WebApp available on http://{}:{}", InetAddress.getLocalHost().getCanonicalHostName(), port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error("Can't start WebApp server", e);
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

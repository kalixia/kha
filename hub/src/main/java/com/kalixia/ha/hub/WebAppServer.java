package com.kalixia.ha.hub;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public class WebAppServer {
    private ServerBootstrap apiBootstrap;
    private final int port;
    private OioEventLoopGroup parentGroup;
    private OioEventLoopGroup childGroup;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAppServer.class);

    public WebAppServer(int port) {
        this.port = port;
        System.setProperty("hub.dir", "../../src/main/webapp");
    }

    public void start() {
        apiBootstrap = new ServerBootstrap();
        parentGroup = new OioEventLoopGroup();
        childGroup = new OioEventLoopGroup();
        try {
            // the hub will only have a few connections, so OIO is likely to be faster than NIO in this case!
            apiBootstrap.group(parentGroup, childGroup)
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
                            String baseDir = getClass().getClassLoader().getResource("devices.html").getPath();
                            baseDir = baseDir.substring(0, baseDir.lastIndexOf('/'));
                            pipeline.addLast("file-handler", new HttpStaticFileServerHandler(baseDir, true));
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
        parentGroup.shutdownGracefully();
        childGroup.shutdownGracefully();
        parentGroup.awaitTermination(1, TimeUnit.MINUTES);
        childGroup.awaitTermination(1, TimeUnit.MINUTES);
    }
}

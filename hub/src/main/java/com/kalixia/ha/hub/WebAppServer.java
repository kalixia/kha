package com.kalixia.ha.hub;

import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.hub.http.SmartHttpContentCompressor;
import com.sun.org.apache.xalan.internal.xsltc.trax.SmartTransformerFactoryImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsHandler;
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
    private final CorsConfig corsConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebAppServer.class);

    public WebAppServer(int port) {
        this.port = port;
        String hubHomeDirectory = System.getProperty("app.home");
        if (hubHomeDirectory.isEmpty()) {
            this.hubSiteDirectory = "src/main/webapp";
        } else {
            this.hubSiteDirectory = hubHomeDirectory + "/site";
        }
        corsConfig = CorsConfig.withAnyOrigin()
                        .allowCredentials()                                     // required for custom headers
                        .allowedRequestMethods(
                                HttpMethod.GET,
                                HttpMethod.POST,
                                HttpMethod.PUT,
                                HttpMethod.DELETE,
                                HttpMethod.OPTIONS)
                        .maxAge(1 * 60 * 60)                                    // 1 hour
                        .allowedRequestHeaders(
                                HttpHeaders.Names.CONTENT_TYPE,
                                RESTCodec.HEADER_REQUEST_ID,                    // header for tracking request ID
                                HttpHeaders.Names.AUTHORIZATION)                // header for OAuth2 authentication
                        .exposeHeaders(RESTCodec.HEADER_REQUEST_ID)
                        .build();
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
                            pipeline.addLast("http-response-encoder", new HttpResponseEncoder());
                            pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(1048576));
                            pipeline.addLast("deflater", new HttpContentDecompressor());
//                            pipeline.addLast("inflater", new HttpContentCompressor());
//                            pipeline.addLast("inflater", new SmartHttpContentCompressor());
                            pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
                            pipeline.addLast("cors", new CorsHandler(corsConfig));
                            pipeline.addLast("file-handler", new HttpStaticFileServerHandler(hubSiteDirectory));
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

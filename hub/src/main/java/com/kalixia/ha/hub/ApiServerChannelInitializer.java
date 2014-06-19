package com.kalixia.ha.hub;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.grapi.codecs.ApiProtocolSwitcher;
import com.kalixia.grapi.codecs.json.ByteBufSerializer;
import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.grapi.codecs.shiro.ShiroHandler;
import com.kalixia.ha.api.rest.GeneratedJaxRsModuleHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.shiro.mgt.SecurityManager;

import javax.inject.Inject;

public class ApiServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ChannelHandler apiProtocolSwitcher;
    private final ShiroHandler shiroHandler;
    private final UserLoggingHandler userLoggingHandler;
    private final GeneratedJaxRsModuleHandler jaxRsHandlers;
    private static final ChannelHandler apiRequestLogger = new LoggingHandler(RESTCodec.class, LogLevel.DEBUG);

    @Inject
    public ApiServerChannelInitializer(ObjectMapper objectMapper,
                                       ApiProtocolSwitcher apiProtocolSwitcher,
                                       SecurityManager securityManager,
                                       GeneratedJaxRsModuleHandler jaxRsModuleHandler) {
        this.apiProtocolSwitcher = apiProtocolSwitcher;
        this.jaxRsHandlers =  jaxRsModuleHandler;
        this.shiroHandler = new ShiroHandler(securityManager);
        this.userLoggingHandler = new UserLoggingHandler();
        SimpleModule nettyModule = new SimpleModule("Netty", PackageVersion.VERSION);
        nettyModule.addSerializer(new ByteBufSerializer());
        objectMapper.registerModule(nettyModule);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("http-request-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-response-encoder", new HttpResponseEncoder());
        pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(1048576));
//        pipeline.addLast("chunked-writer", new ChunkedWriteHandler());
//        pipeline.addLast("deflater", new HttpContentDecompressor());      // TODO: seems to cause issue with request processed twice!
//        pipeline.addLast("inflater", new HttpContentCompressor());
        pipeline.addLast("shiro", shiroHandler);
        pipeline.addLast("user-logging-handler", userLoggingHandler);

        // Alters the pipeline depending on either REST or WebSockets requests
        pipeline.addLast("api-protocol-switcher", apiProtocolSwitcher);

        // Logging handlers for API requests
        pipeline.addLast("api-request-logger", apiRequestLogger);

        // JAX-RS handlers
        pipeline.addLast("jax-rs-handlers", jaxRsHandlers);
    }

}

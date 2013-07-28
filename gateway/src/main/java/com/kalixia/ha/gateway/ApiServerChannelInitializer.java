package com.kalixia.ha.gateway;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.grapi.codecs.ApiProtocolSwitcher;
import com.kalixia.grapi.codecs.json.ByteBufSerializer;
import com.kalixia.grapi.codecs.rest.RESTCodec;
import com.kalixia.ha.api.rest.GeneratedJaxRsModuleHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import javax.inject.Inject;

public class ApiServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ChannelHandler apiProtocolSwitcher;
//    private final ObservableEncoder rxjavaHandler;
    private final GeneratedJaxRsModuleHandler jaxRsHandlers;
//    private final EventExecutorGroup rxJavaGroup;
    private static final ChannelHandler debugger = new LoggingHandler(LogLevel.TRACE);
    private static final ChannelHandler apiRequestLogger = new LoggingHandler(RESTCodec.class, LogLevel.DEBUG);

    @Inject
    public ApiServerChannelInitializer(ObjectMapper objectMapper,
                                       ApiProtocolSwitcher apiProtocolSwitcher,
//                                       ObservableEncoder rxjavaHandler,
                                       GeneratedJaxRsModuleHandler jaxRsModuleHandler) {
        this.apiProtocolSwitcher = apiProtocolSwitcher;
//        this.rxjavaHandler = rxjavaHandler;
        this.jaxRsHandlers =  jaxRsModuleHandler;
        SimpleModule nettyModule = new SimpleModule("Netty", PackageVersion.VERSION);
        nettyModule.addSerializer(new ByteBufSerializer());
        objectMapper.registerModule(nettyModule);
        // TODO: allow customization of the thread pool!
//        rxJavaGroup = new DefaultEventExecutorGroup(4, new DefaultThreadFactory("rxjava"));
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("http-request-decoder", new HttpRequestDecoder());
        pipeline.addLast("deflater", new HttpContentDecompressor());
        pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("http-response-encoder", new HttpResponseEncoder());
        pipeline.addLast("inflater", new HttpContentCompressor());

        // Alters the pipeline depending on either REST or WebSockets requests
        pipeline.addLast("api-protocol-switcher", apiProtocolSwitcher);
        pipeline.addLast("debugger", debugger);

        // Logging handlers for API requests
        pipeline.addLast("api-request-logger", apiRequestLogger);

//        pipeline.addLast(rxJavaGroup, "rxjava-handler", rxjavaHandler);

        // JAX-RS handlers
        pipeline.addLast("jax-rs-handler", jaxRsHandlers);
    }
}

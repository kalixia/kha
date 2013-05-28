package com.kalixia.ha.gateway;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.ha.api.rest.GeneratedJaxRsModuleHandler;
import com.kalixia.netty.rest.codecs.json.ByteBufSerializer;
import com.kalixia.netty.rest.codecs.rest.RESTCodec;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.MessageLoggingHandler;

import javax.inject.Inject;

public class ApiServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ObjectMapper objectMapper;
    private final GeneratedJaxRsModuleHandler jaxRsHandlers;
    private static final ChannelHandler debugger = new MessageLoggingHandler(LogLevel.TRACE);
    private static final ChannelHandler apiRequestLogger = new MessageLoggingHandler(RESTCodec.class, LogLevel.DEBUG);

    @Inject
    public ApiServerChannelInitializer(ObjectMapper objectMapper, GeneratedJaxRsModuleHandler jaxRsModuleHandler) {
        this.objectMapper = objectMapper;
        this.jaxRsHandlers =  jaxRsModuleHandler;
        SimpleModule nettyModule = new SimpleModule("Netty", PackageVersion.VERSION);
        nettyModule.addSerializer(new ByteBufSerializer());
        objectMapper.registerModule(nettyModule);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("http-request-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("http-response-encoder", new HttpResponseEncoder());

        // Alters the pipeline depending on either REST or WebSockets requests
        pipeline.addLast("api-protocol-switcher", new ApiProtocolSwitcher(objectMapper));
        pipeline.addLast("debugger", debugger);

        // Logging handlers for API requests
        pipeline.addLast("api-request-logger", apiRequestLogger);

        // JAX-RS handlers
        pipeline.addLast("jax-rs-handler", jaxRsHandlers);
    }
}

package com.kalixia.ha.gateway;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.ha.gateway.codecs.jaxrs.JaxRsHandler;
import com.kalixia.ha.gateway.codecs.json.ByteBufSerializer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.MessageLoggingHandler;
import org.scannotation.AnnotationDB;

import java.io.IOException;

public class ApiServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ObjectMapper objectMapper;
    private final ChannelHandler jaxRsHandler;

    public ApiServerChannelInitializer() throws AnnotationDB.CrossReferenceException, IOException, ClassNotFoundException {
        this.objectMapper = new ObjectMapper();
        SimpleModule nettyModule = new SimpleModule("Netty", new Version(1, 0, 0, null));
        nettyModule.addSerializer(new ByteBufSerializer());
        objectMapper.registerModule(nettyModule);
        jaxRsHandler = new JaxRsHandler(objectMapper);
    }

    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("http-request-decoder", new HttpRequestDecoder());
        pipeline.addLast("http-object-aggregator", new HttpObjectAggregator(1048576));
        pipeline.addLast("http-response-encoder", new HttpResponseEncoder());

        // Alters the pipeline depending on either REST or WebSockets requests
        pipeline.addLast("api-protocol-switcher", new ApiProtocolSwitcher(objectMapper));
        pipeline.addLast("debugger", new MessageLoggingHandler(LogLevel.TRACE));

        // Logging handlers for API requests
        pipeline.addLast("api-request-logger", new MessageLoggingHandler(ApiRequest.class, LogLevel.DEBUG));

        // JAX-RS handlers
        pipeline.addLast("jax-rs-handler", jaxRsHandler);
    }
}

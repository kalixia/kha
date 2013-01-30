package com.kalixia.ha.gateway;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.ha.gateway.codecs.jaxrs.JaxRsHandler;
import com.kalixia.ha.gateway.codecs.json.ByteBufSerializer;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.MessageLoggingHandler;

public class ApiServerChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final ObjectMapper objectMapper;

    public ApiServerChannelInitializer() {
        this.objectMapper = new ObjectMapper();
        SimpleModule nettyModule = new SimpleModule("Netty", new Version(1, 0, 0, null));
        nettyModule.addSerializer(new ByteBufSerializer());
        objectMapper.registerModule(nettyModule);
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());

        // Alters the pipeline depending on either REST or WebSockets requests
        pipeline.addLast("api-protocol-switcher", new ApiProtocolSwitcher(objectMapper));

        // Logging handlers for API requests
        pipeline.addLast("api-request-logger", new MessageLoggingHandler());

        // JAX-RS handlers
        pipeline.addLast("jax-rs-handler", new JaxRsHandler(objectMapper));
    }
}

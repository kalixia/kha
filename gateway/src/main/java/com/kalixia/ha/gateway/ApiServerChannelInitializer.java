package com.kalixia.ha.gateway;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.ha.gateway.codecs.jaxrs.JaxRsHandler;
import com.kalixia.ha.gateway.codecs.json.ByteBufSerializer;
import com.kalixia.ha.gateway.codecs.websockets.WebSocketsApiRequestDecoder;
import com.kalixia.ha.gateway.codecs.websockets.WebSocketsApiResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

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

        pipeline.addLast("api-protocol-switcher", new ApiProtocolSwitcher(objectMapper));

        pipeline.addLast("api-response-encoder-ws", new WebSocketsApiResponseEncoder(objectMapper));
        pipeline.addLast("api-request-decoder-ws", new WebSocketsApiRequestDecoder(objectMapper));

//        pipeline.addLast("api-request-handler", new ApiRequestHandler());
        pipeline.addLast("jax-rs-handler", new JaxRsHandler(objectMapper));
    }
}

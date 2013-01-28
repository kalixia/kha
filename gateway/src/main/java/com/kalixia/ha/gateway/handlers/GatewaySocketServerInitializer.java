package com.kalixia.ha.gateway.handlers;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kalixia.ha.gateway.handlers.ApiProtocolSwitcher;
import com.kalixia.ha.gateway.handlers.ApiRequestHandler;
import com.kalixia.ha.gateway.websockets.WebSocketsApiRequestDecoder;
import com.kalixia.ha.gateway.websockets.WebSocketsApiResponseEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

public class GatewaySocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ObjectMapper objectMapper;

    public GatewaySocketServerInitializer() {
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

        pipeline.addLast("api-request-handler", new ApiRequestHandler());
    }
}

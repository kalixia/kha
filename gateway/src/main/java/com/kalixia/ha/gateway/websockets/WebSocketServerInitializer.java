package com.kalixia.ha.gateway.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.handlers.ApiProtocolSwitcher;
import com.kalixia.ha.gateway.handlers.ApiRequestHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private final ObjectMapper objectMapper;

    WebSocketServerInitializer() {
        this.objectMapper = new ObjectMapper();
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

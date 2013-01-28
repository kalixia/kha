package com.kalixia.ha.gateway.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
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
//        pipeline.addLast("handler", new WebSocketServerHandler());
//        pipeline.addLast("api-request-encoder-ws", new WebSocketsApiRequestEncoder(objectMapper));
        pipeline.addLast("web-sockets-client-page", new WebSocketsClientPage());
    }
}

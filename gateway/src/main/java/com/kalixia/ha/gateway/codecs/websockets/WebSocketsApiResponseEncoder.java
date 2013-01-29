package com.kalixia.ha.gateway.codecs.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.ApiResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketsApiResponseEncoder extends MessageToMessageEncoder<ApiResponse> {
    private final ObjectMapper objectMapper;

    public WebSocketsApiResponseEncoder(ObjectMapper objectMapper) {
        super(ApiResponse.class);
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, ApiResponse msg) throws Exception {
        String json = objectMapper.writeValueAsString(msg);
        return new TextWebSocketFrame(json);
    }
}

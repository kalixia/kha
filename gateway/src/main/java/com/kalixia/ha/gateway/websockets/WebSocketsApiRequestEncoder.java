package com.kalixia.ha.gateway.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.ApiRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

import java.util.UUID;

/**
 * Transforms {@link WebSocketFrame} objects to {@link ApiRequest} ones.
 */
class WebSocketsApiRequestEncoder extends MessageToMessageEncoder<WebSocketFrame> {
    private final ObjectMapper mapper;

    public WebSocketsApiRequestEncoder(ObjectMapper mapper, Class<?>... acceptedMsgTypes) {
        super(acceptedMsgTypes);
        this.mapper = mapper;
    }

    @Override
    protected Object encode(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

            WebSocketRequest wsRequest = mapper.readValue(textFrame.text(), WebSocketRequest.class);
            ByteBuf content = Unpooled.copiedBuffer(wsRequest.getEntity().getBytes(CharsetUtil.UTF_8));
            return new ApiRequest(UUID.randomUUID(), wsRequest.getPath(),
                    HttpMethod.valueOf(wsRequest.getMethod()), content);
        } else {
            return null;
        }
    }

}

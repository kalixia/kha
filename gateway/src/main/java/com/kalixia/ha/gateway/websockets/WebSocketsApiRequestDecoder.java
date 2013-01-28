package com.kalixia.ha.gateway.websockets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.ApiRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Transforms {@link WebSocketFrame} objects to {@link ApiRequest} ones.
 */
public class WebSocketsApiRequestDecoder extends MessageToMessageDecoder<WebSocketFrame> {
    private final ObjectMapper mapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketsApiRequestDecoder.class);

    public WebSocketsApiRequestDecoder(ObjectMapper mapper) {
        super(WebSocketFrame.class);
        this.mapper = mapper;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

            WebSocketRequest wsRequest = mapper.readValue(textFrame.text(), WebSocketRequest.class);
            ByteBuf content = Unpooled.copiedBuffer(wsRequest.getEntity().getBytes(CharsetUtil.UTF_8));

            UUID requestID = wsRequest.getId() != null ? wsRequest.getId() : UUID.randomUUID();
            return new ApiRequest(requestID, wsRequest.getPath(),
                    HttpMethod.valueOf(wsRequest.getMethod()), content);
        } else {
            return null;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Can't convert WebSockets request to API request", cause);
        ctx.close();
    }
}

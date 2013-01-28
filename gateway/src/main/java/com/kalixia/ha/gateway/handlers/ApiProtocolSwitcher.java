package com.kalixia.ha.gateway.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.websockets.WebSocketsServerProtocolUpdater;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Alters the pipeline in order to cope with both HTTP REST API handlers and WebSockets handlers.
 */
public class ApiProtocolSwitcher extends MessageToMessageDecoder<Object> {
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiProtocolSwitcher.class);

    public ApiProtocolSwitcher(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            if (((FullHttpRequest) msg).uri().equals("/websocket")) {
                LOGGER.info("Should add WebSockets API handlers...");
                ctx.pipeline().addLast("web-sockets-client-page", new WebSocketsServerProtocolUpdater());
                ctx.pipeline().remove(ApiProtocolSwitcher.class);
            } else {
                LOGGER.info("Should add REST API handlers...");
                // TODO: add REST API handlers
            }
        } else {
            LOGGER.warn("Ignoring {}...", msg.getClass().getName());
        }
        return msg;
    }
}

package com.kalixia.ha.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.ha.gateway.codecs.jaxrs.JaxRsHandler;
import com.kalixia.ha.gateway.codecs.rest.RESTRequestDecoder;
import com.kalixia.ha.gateway.codecs.rest.RESTResponseEncoder;
import com.kalixia.ha.gateway.codecs.websockets.WebSocketsApiRequestDecoder;
import com.kalixia.ha.gateway.codecs.websockets.WebSocketsApiResponseEncoder;
import com.kalixia.ha.gateway.codecs.websockets.WebSocketsServerProtocolUpdater;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.MessageLoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetSocketAddress;

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
            ChannelPipeline pipeline = ctx.pipeline();
            if (((FullHttpRequest) msg).getUri().equals("/websocket")) {
                LOGGER.debug("Switching to WebSockets pipeline...");
                pipeline.addAfter("api-protocol-switcher", "ws-protocol-updater", new WebSocketsServerProtocolUpdater());
                pipeline.addAfter("ws-protocol-updater", "api-response-encoder-ws", new WebSocketsApiResponseEncoder());
                pipeline.addAfter("api-response-encoder-ws", "api-request-decoder-ws", new WebSocketsApiRequestDecoder(objectMapper));
                pipeline.removeAndForward(ApiProtocolSwitcher.class);
            } else {
                LOGGER.debug("Switching to REST pipeline...");
                pipeline.addAfter("api-protocol-switcher", "api-response-encoder-rest", new RESTResponseEncoder());
                pipeline.addAfter("api-response-encoder-rest", "api-request-decoder-rest", new RESTRequestDecoder());
                pipeline.removeAndForward(ApiProtocolSwitcher.class);
            }
        } else {
            LOGGER.warn("Ignoring {}...", msg.getClass().getName());
            LOGGER.info("Pipeline {}", ctx.pipeline());
        }
        return msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        MDC.put(MDCLogging.MDC_CLIENT_ADDR, ((InetSocketAddress) ctx.channel().remoteAddress()).getHostString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        MDC.remove(MDCLogging.MDC_CLIENT_ADDR);
    }
}

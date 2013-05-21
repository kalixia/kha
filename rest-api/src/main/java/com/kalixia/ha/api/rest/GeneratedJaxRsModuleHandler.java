package com.kalixia.ha.api.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalixia.netty.rest.ApiRequest;
import com.kalixia.netty.rest.ApiResponse;
import com.kalixia.netty.rest.codecs.jaxrs.GeneratedJaxRsMethodHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ChannelHandler.Sharable
public class GeneratedJaxRsModuleHandler extends MessageToMessageDecoder<ApiRequest> {
    private final Set<? extends GeneratedJaxRsMethodHandler> handlers;
    private static final ByteBuf ERROR_WRONG_URL = Unpooled.copiedBuffer("Wrong URL", Charset.forName("UTF-8"));
    private static final ByteBuf ERROR_INTERNAL_ERROR = Unpooled.copiedBuffer("Unexpected error", Charset.forName("UTF-8"));
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratedJaxRsModuleHandler.class);

    public GeneratedJaxRsModuleHandler(ObjectMapper objectMapper) {
        this.handlers = new HashSet<>(Arrays.asList(
                new HelloResourceHELLOHandler(objectMapper),
                new HelloResourceHELLO2Handler(objectMapper),
                new EchoResourceECHOHandler(objectMapper),
                new DoNothingResourceDONOTHINGHandler(objectMapper),
                new ObjectsResourceECHOUUIDHandler(objectMapper)
        ));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ApiRequest request, MessageBuf<Object> out) throws Exception {
        for (GeneratedJaxRsMethodHandler handler : handlers) {
            if (handler.matches(request)) {
                try {
                    ApiResponse response = handler.handle(request);
                    ctx.write(response);
                    return;
                } catch (Exception e) {
                    LOGGER.error("Can't invoke JAX-RS resource", e);
                    ctx.write(new ApiResponse(request.id(), HttpResponseStatus.INTERNAL_SERVER_ERROR,
                            ERROR_INTERNAL_ERROR, MediaType.TEXT_PLAIN));
                    return;
                }
            }
        }
        // no matching handler found -- issue a 404 error
        LOGGER.info("Could not locate a JAX-RS resource for path '{}' and method {}", request.uri(), request.method());

        ctx.write(new ApiResponse(request.id(), HttpResponseStatus.NOT_FOUND, ERROR_WRONG_URL, MediaType.TEXT_PLAIN));
    }
}

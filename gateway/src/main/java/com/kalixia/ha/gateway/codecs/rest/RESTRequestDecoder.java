package com.kalixia.ha.gateway.codecs.rest;

import com.kalixia.ha.gateway.ApiRequest;
import com.kalixia.ha.gateway.MDCLogging;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.net.InetSocketAddress;
import java.util.UUID;

public class RESTRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTRequestDecoder.class);

    public RESTRequestDecoder() {
        super(DefaultFullHttpRequest.class);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        UUID requestID;
        String requestIDasString = request.headers().get("X-Api-Request-ID");
        if (requestIDasString != null && "".equals(requestIDasString)) {
            requestID = UUID.fromString(requestIDasString);
        } else {
            requestID = UUID.randomUUID();
        }
        MDC.put(MDCLogging.MDC_REQUEST_ID, requestID.toString());

        String contentType = request.headers().get(HttpHeaders.Names.ACCEPT);

        InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return new ApiRequest(requestID,
                request.getUri(), request.getMethod(),
                request.data(), contentType,
                clientAddress.getHostName());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Can't convert REST request to API request", cause);
        ctx.close();
    }

}

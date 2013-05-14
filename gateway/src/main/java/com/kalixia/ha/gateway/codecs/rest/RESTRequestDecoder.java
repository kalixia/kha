package com.kalixia.ha.gateway.codecs.rest;

import com.kalixia.ha.gateway.ApiRequest;
import com.kalixia.ha.gateway.MDCLogging;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandler;
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

@ChannelHandler.Sharable
public class RESTRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTRequestDecoder.class);

//    public RESTRequestDecoder() {
//        super(FullHttpRequest.class);
//        super(DefaultFullHttpRequest.class);
//    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, MessageBuf<Object> out) throws Exception {
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
        out.add(new ApiRequest(requestID,
                request.getUri(), request.getMethod(),
                request.content(), contentType,
                clientAddress.getHostName()));
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Can't convert REST request to API request", cause);
        ctx.close();
    }

}

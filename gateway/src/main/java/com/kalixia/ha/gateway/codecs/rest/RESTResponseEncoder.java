package com.kalixia.ha.gateway.codecs.rest;

import com.kalixia.ha.gateway.ApiResponse;
import io.netty.buffer.MessageBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;

@ChannelHandler.Sharable
public class RESTResponseEncoder extends MessageToMessageEncoder<ApiResponse> {

    public RESTResponseEncoder() {
        super(ApiResponse.class);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ApiResponse apiResponse, MessageBuf<Object> out) throws Exception {
        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,       // TODO: reply with the expectations from the request
                apiResponse.status(),
                apiResponse.content());
        // insert usual HTTP headers
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_LENGTH, apiResponse.content().readableBytes());
        httpResponse.headers().set(HttpHeaders.Names.CONTENT_TYPE, apiResponse.contentType());
        httpResponse.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        // insert request ID header
        if (apiResponse.id() != null) {
            httpResponse.headers().set("X-Api-Request-ID", apiResponse.id().toString());
        }
        out.add(httpResponse);
    }
}

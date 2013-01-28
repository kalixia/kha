package com.kalixia.ha.gateway.handlers;

import com.kalixia.ha.gateway.ApiRequest;
import com.kalixia.ha.gateway.ApiResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiRequestHandler extends ChannelInboundMessageHandlerAdapter<ApiRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRequestHandler.class);

    public ApiRequestHandler() {
        super(ApiRequest.class);
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, ApiRequest request) throws Exception {
        LOGGER.info("Received API request {}", request);
        ctx.write(new ApiResponse(request.getId(), request.getContent()));
    }
}

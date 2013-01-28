package com.kalixia.ha.gateway;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ApiResponse extends ApiObject {

    public ApiResponse(UUID id, ByteBuf content) {
        super(id, content);
    }

}

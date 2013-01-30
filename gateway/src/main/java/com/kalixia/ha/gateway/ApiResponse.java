package com.kalixia.ha.gateway;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ApiResponse extends ApiObject {

    public ApiResponse(UUID id, ByteBuf content, String contentType) {
        super(id, content, contentType);
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ApiResponse");
        sb.append("{id=").append(id());
        sb.append(", content=").append(content());
        sb.append('}');
        return sb.toString();
    }

}

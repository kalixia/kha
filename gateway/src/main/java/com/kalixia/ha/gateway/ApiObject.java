package com.kalixia.ha.gateway;

import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class ApiObject {
    private final UUID id;
    private ByteBuf content;

    public ApiObject(UUID id, ByteBuf content) {
        this.id = id;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public ByteBuf getContent() {
        return content;
    }
}

package com.kalixia.ha.gateway;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;

import java.util.UUID;

/**
 * Request to the API.
 *
 * Created from either the REST API or the WebSockets API.
 *
 * This class is intentionally immutable.
 */
public class ApiRequest {
    private final UUID id;
    private final String path;
    private final HttpMethod method;
    private ByteBuf content;

    public ApiRequest(UUID id, String path, HttpMethod method, ByteBuf content) {
        this.id = id;
        this.path = path;
        this.method = method;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public ByteBuf getContent() {
        return content;
    }
}

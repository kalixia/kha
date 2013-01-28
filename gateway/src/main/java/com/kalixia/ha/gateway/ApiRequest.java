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
public class ApiRequest extends ApiObject {
    private final String path;
    private final HttpMethod method;
    private final String clientAddress;

    public ApiRequest(UUID id, String path, HttpMethod method, ByteBuf content, String clientAddress) {
        super(id, content);
        this.path = path;
        this.method = method;
        this.clientAddress = clientAddress;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ApiRequest");
        sb.append("{id=").append(getId());
        sb.append(", path='").append(path).append('\'');
        sb.append(", method=").append(method);
        sb.append(", content=").append(getContent());
        sb.append(", clientAddress=").append(getClientAddress());
        sb.append('}');
        return sb.toString();
    }
}

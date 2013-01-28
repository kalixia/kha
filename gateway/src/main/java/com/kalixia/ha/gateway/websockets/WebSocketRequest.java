package com.kalixia.ha.gateway.websockets;

/**
 * Models the content expected from the WebSockets clients.
 */
class WebSocketRequest {
    private final String path;
    private final String method;
    private final String entity;

    public WebSocketRequest(String path, String method, String entity) {
        this.path = path;
        this.method = method;
        this.entity = entity;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getEntity() {
        return entity;
    }
}

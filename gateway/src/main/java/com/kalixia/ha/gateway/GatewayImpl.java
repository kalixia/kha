package com.kalixia.ha.gateway;

import com.kalixia.ha.gateway.websockets.WebSocketsApiServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayImpl implements Gateway {
    private final WebSocketsApiServer wsServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

    public GatewayImpl() {
        this.wsServer = new WebSocketsApiServer(8081);
    }

    @Override
    public void start() throws InterruptedException {
        startApiREST();
        startApiWS();
        startCloudRelay();
    }

    @Override
    public void stop() {
        stopCloudRelay();
        stopApiWS();
        stopApiREST();
    }

    private void startApiREST() {
        LOGGER.info("Starting REST API...");
        // TODO: write code!
    }

    private void startApiWS() throws InterruptedException {
        LOGGER.info("Starting WebSockets API...");
        wsServer.start();
    }

    private void startCloudRelay() {
        LOGGER.info("Starting Cloud Relay...");
        // TODO: write code!
    }

    private void stopApiREST() {
        LOGGER.info("Stopping REST API...");
        wsServer.stop();
    }

    private void stopApiWS() {
        LOGGER.info("Stopping WebSockets API...");
        // TODO: write code!
    }

    private void stopCloudRelay() {
        LOGGER.info("Stopping Cloud Relay...");
        // TODO: write code!
    }

}

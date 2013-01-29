package com.kalixia.ha.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@inheritDoc}
 *
 * The implementation transforms either REST or WebSockets requests to <em>universal</em> API requests.
 * So basically, this implementation constructs {@link ApiRequest}s from the underlying protocols.
 *
 */
public class GatewayImpl implements Gateway {
    private final ApiServer wsServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

    public GatewayImpl() {
        this.wsServer = new ApiServer(8081);
    }

    @Override
    public void start() throws InterruptedException {
        startApi();
        startCloudRelay();
    }

    @Override
    public void stop() {
        stopCloudRelay();
        stopApi();
    }

    private void startApi() throws InterruptedException {
        LOGGER.info("Starting API Server...");
        wsServer.start();
    }

    private void startCloudRelay() {
        LOGGER.info("Starting Cloud Relay...");
        // TODO: write code!
    }

    private void stopApi() {
        LOGGER.info("Stopping API Server...");
        wsServer.stop();
    }

    private void stopCloudRelay() {
        LOGGER.info("Stopping Cloud Relay...");
        // TODO: write code!
    }

}

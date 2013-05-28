package com.kalixia.ha.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * {@inheritDoc}
 *
 * The implementation transforms either REST or WebSockets requests to <em>universal</em> API requests.
 * So basically, this implementation constructs {@link ApiRequest}s from the underlying protocols.
 *
 */
public class GatewayImpl implements Gateway {
    private final ApiServer apiServer;
    private static final Logger LOGGER = LoggerFactory.getLogger(Gateway.class);

    @Inject
    public GatewayImpl(ApiServer apiServer) {
        this.apiServer = apiServer;
    }

    @Override
    public void start() {
        startApi();
        startCloudRelay();
    }

    @Override
    public void stop() throws InterruptedException {
        stopCloudRelay();
        stopApi();
    }

    private void startApi() {
        LOGGER.info("Starting API Server...");
        apiServer.start();
    }

    private void startCloudRelay() {
        LOGGER.info("Starting Cloud Relay...");
        // TODO: write code!
    }

    private void stopApi() throws InterruptedException {
        LOGGER.info("Stopping API Server...");
        apiServer.stop();
    }

    private void stopCloudRelay() {
        LOGGER.info("Stopping Cloud Relay...");
        // TODO: write code!
    }

}

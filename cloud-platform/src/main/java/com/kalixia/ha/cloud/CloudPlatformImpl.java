package com.kalixia.ha.cloud;

import com.codahale.metrics.graphite.GraphiteReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * {@inheritDoc}
 *
 * The implementation transforms either REST or WebSockets requests to <em>universal</em> API requests.
 * So basically, this implementation constructs {@link ApiRequest}s from the underlying protocols.
 *
 */
public class CloudPlatformImpl implements CloudPlatform {
    private final ApiServer apiServer;
    private final WebAppServer webAppServer;
    private final GraphiteReporter graphiteReporter;
    private static final Logger LOGGER = LoggerFactory.getLogger(CloudPlatform.class);

    @Inject
    public CloudPlatformImpl(ApiServer apiServer, WebAppServer webAppServer, GraphiteReporter graphiteReporter) {
        this.apiServer = apiServer;
        this.webAppServer = webAppServer;
        this.graphiteReporter = graphiteReporter;
    }

    @Override
    public void start() {
        graphiteReporter.start(15, TimeUnit.SECONDS);
        startApi();
        startCloudRelay();
        startWebApp();
    }

    @Override
    public void stop() throws InterruptedException {
        stopCloudRelay();
        stopApi();
        stopWebApp();
        graphiteReporter.stop();
    }

    private void startApi() {
        LOGGER.info("Starting API Server...");
        apiServer.start();
    }

    private void startCloudRelay() {
        LOGGER.info("Starting Cloud Relay...");
        // TODO: write code!
    }

    private void startWebApp() {
        LOGGER.info("Starting WebApp...");
        webAppServer.start();
    }

    private void stopApi() throws InterruptedException {
        LOGGER.info("Stopping API Server...");
        apiServer.stop();
    }

    private void stopCloudRelay() {
        LOGGER.info("Stopping Cloud Relay...");
        // TODO: write code!
    }

    private void stopWebApp() throws InterruptedException {
        LOGGER.info("Stopping WebApp...");
        webAppServer.stop();
    }

}

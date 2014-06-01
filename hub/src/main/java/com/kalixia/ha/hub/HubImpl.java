package com.kalixia.ha.hub;

import com.codahale.metrics.JmxReporter;
import com.kalixia.ha.api.UsersService;
import org.apache.shiro.mgt.SecurityManager;
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
public class HubImpl implements Hub {
    private final ApiServer apiServer;
    private final WebAppServer webAppServer;
    private final UsersService usersService;
    private final SecurityManager securityManager;
    private final JmxReporter jmxReporter;
    private static final Logger LOGGER = LoggerFactory.getLogger(Hub.class);

    @Inject
    public HubImpl(ApiServer apiServer, WebAppServer webAppServer, UsersService usersService,
                   SecurityManager securityManager, JmxReporter jmxReporter) {
        this.apiServer = apiServer;
        this.webAppServer = webAppServer;
        this.usersService = usersService;
        this.securityManager = securityManager;
        this.jmxReporter = jmxReporter;
    }

    @Override
    public void start() {
        jmxReporter.start();
        startApi();
        startCloudRelay();
        startWebApp();
    }

    @Override
    public void stop() throws InterruptedException {
        stopCloudRelay();
        stopApi();
        stopWebApp();
        jmxReporter.stop();
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

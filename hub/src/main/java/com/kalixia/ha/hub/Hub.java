package com.kalixia.ha.hub;

/**
 * A hub provides many different services:
 * <ul>
 *     <li>a REST API,</li>
 *     <li>a WebSockets API,</li>
 *     <li>a relay to a Cloud platform in order to route messages to/from the Cloud</li>
 * </ul>
 */
public interface Hub {
    void start();
    void stop() throws InterruptedException;
}

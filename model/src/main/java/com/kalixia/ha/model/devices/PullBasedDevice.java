package com.kalixia.ha.model.devices;

/**
 * Device whose sensors needs to be pulled.
 * This kind of device needs sensors data to be retreived as it is not pushed into the gateway.
 */
public interface PullBasedDevice extends Device {
    void fetchSensorsData();
}

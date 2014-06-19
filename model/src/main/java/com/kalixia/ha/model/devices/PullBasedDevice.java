package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.configuration.Configuration;

/**
 * Device whose sensors needs to be pulled.
 * This kind of device needs sensors data to be retreived as it is not pushed into the gateway.
 */
public interface PullBasedDevice<C extends Configuration> extends Device<C> {
    void fetchSensorsData();
}

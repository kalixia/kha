package com.kalixia.ha.model.devices;

import com.kalixia.ha.model.configuration.Configuration;

/**
 * Device which needs configuration data.
 */
public interface ConfigurableDevice<C extends Configuration> extends Device {
    void init(C configuration);
}

package com.kalixia.ha.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Basic configuration for sensor.
 */
public class SensorConfiguration extends Configuration {
    @JsonProperty("enabled")
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }
}

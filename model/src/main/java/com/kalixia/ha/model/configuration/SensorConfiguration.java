package com.kalixia.ha.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Basic configuration for sensor.
 */
public class SensorConfiguration extends Configuration {
    @JsonProperty("name")
    private String name;

    @JsonProperty("enabled")
    private boolean enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

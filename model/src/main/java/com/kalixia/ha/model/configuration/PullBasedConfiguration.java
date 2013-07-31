package com.kalixia.ha.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for {@link com.kalixia.ha.model.devices.PullBasedDevice}s.
 */
public class PullBasedConfiguration extends Configuration {
    @JsonProperty("interval")
    private String pollingInterval;

    public String getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(String pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}

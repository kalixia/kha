package com.kalixia.ha.devices.gce.ecodevices

import com.fasterxml.jackson.annotation.JsonProperty
import com.kalixia.ha.model.configuration.AuthenticationConfiguration
import com.kalixia.ha.model.configuration.Configuration
import com.kalixia.ha.model.configuration.PullBasedConfiguration
import com.kalixia.ha.model.configuration.SensorConfiguration

/**
 * Configuration for the Eco Device.
 */
public class EcoDeviceConfiguration extends Configuration {
    @JsonProperty("url")
    private String url

    @JsonProperty("authentication")
    private AuthenticationConfiguration authentication

    @JsonProperty("power1")
    private SensorConfiguration power1

    @JsonProperty("power2")
    private SensorConfiguration power2

    @JsonProperty("counter1")
    private SensorConfiguration counter1

    @JsonProperty("counter2")
    private SensorConfiguration counter2

    @JsonProperty("polling")
    private PullBasedConfiguration polling

    public String getUrl() {
        return url
    }

    public AuthenticationConfiguration getAuthentication() {
        return authentication
    }

    SensorConfiguration getPower1() {
        return power1
    }

    SensorConfiguration getPower2() {
        return power2
    }

    SensorConfiguration getCounter1() {
        return counter1
    }

    SensorConfiguration getCounter2() {
        return counter2
    }

    PullBasedConfiguration getPolling() {
        return polling
    }
}
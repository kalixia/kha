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
    private AuthenticationConfiguration authenticationConfiguration

    @JsonProperty("power1")
    private SensorConfiguration power1SensorConfiguration

    @JsonProperty("power2")
    private SensorConfiguration power2SensorConfiguration

    @JsonProperty("water")
    private SensorConfiguration waterSensorConfiguration

    @JsonProperty("gaz")
    private SensorConfiguration gazSensorConfiguration

    @JsonProperty("polling")
    private PullBasedConfiguration polling

    public String getUrl() {
        return url
    }

    public AuthenticationConfiguration getAuthenticationConfiguration() {
        return authenticationConfiguration
    }

    public SensorConfiguration getPower1SensorConfiguration() {
        return power1SensorConfiguration
    }

    public SensorConfiguration getPower2SensorConfiguration() {
        return power2SensorConfiguration
    }

    public SensorConfiguration getWaterSensorConfiguration() {
        return waterSensorConfiguration
    }

    public SensorConfiguration getGazSensorConfiguration() {
        return gazSensorConfiguration
    }

    public PullBasedConfiguration getPolling() {
        return polling
    }

}
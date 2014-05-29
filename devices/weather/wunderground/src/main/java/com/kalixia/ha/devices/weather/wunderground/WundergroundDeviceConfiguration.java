package com.kalixia.ha.devices.weather.wunderground;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.kalixia.ha.model.configuration.Configuration;
import com.kalixia.ha.model.configuration.PullBasedConfiguration;

public class WundergroundDeviceConfiguration extends Configuration {
    @JsonProperty("apiKey")
    private String apiKey;

    @JsonProperty("station")
    private String station;

    @JsonProperty("polling")
    private PullBasedConfiguration polling;

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

    public String getApiKey() {
        return apiKey;
    }

    public String getStation() {
        return station;
    }

    public PullBasedConfiguration getPolling() {
        return polling;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

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

    @JsonProperty("city")
    private String city;

    @JsonProperty("state")
    private String state;

    @JsonProperty("country")
    private String country;

    @JsonProperty("polling")
    private PullBasedConfiguration polling = new PullBasedConfiguration();

    @JsonProperty("host")
    private String host;

    @JsonProperty("port")
    private int port;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public PullBasedConfiguration getPolling() {
        return polling;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

package com.kalixia.ha.devices.zibase.zapi2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.kalixia.ha.model.configuration.Configuration;

public class ZibaseDeviceConfiguration extends Configuration {
    @JsonProperty("url")
    private String url;

    @JsonProperty("zibaseID")
    private String zibaseID;

    @JsonProperty("authentication")
    private AuthenticationConfiguration authentication = new AuthenticationConfiguration();

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getZibaseID() {
        return zibaseID;
    }

    public void setZibaseID(String zibaseID) {
        this.zibaseID = zibaseID;
    }

    public AuthenticationConfiguration getAuthentication() {
        return authentication;
    }
}

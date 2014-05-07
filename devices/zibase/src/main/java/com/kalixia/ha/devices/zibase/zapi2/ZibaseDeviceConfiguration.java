package com.kalixia.ha.devices.zibase.zapi2;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kalixia.ha.model.configuration.AuthenticationConfiguration;
import com.kalixia.ha.model.configuration.Configuration;

public class ZibaseDeviceConfiguration extends Configuration {
    @JsonProperty("url")
    private String url;

    @JsonProperty("authentication")
    private AuthenticationConfiguration authentication;

    public String getUrl() {
        return url;
    }

    public AuthenticationConfiguration getAuthentication() {
        return authentication;
    }
}

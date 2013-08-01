package com.kalixia.ha.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration for basic authentication.
 */
public class AuthenticationConfiguration extends Configuration {
    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

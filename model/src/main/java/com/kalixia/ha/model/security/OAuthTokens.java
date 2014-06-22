package com.kalixia.ha.model.security;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.UUID;

/**
 * Tuple of tokens (access token and refresh token) allowing access to the API for 1 client app.
 * A user can have many {@link com.kalixia.ha.model.security.OAuthTokens} as many client apps may
 * require access on user's behalf.
 */
@ApiModel("OAuth2 set of tokens")
public class OAuthTokens implements Serializable {
    @ApiModelProperty(value = "OAuth2 access token", required = true)
    private final String accessToken;

    @ApiModelProperty(value = "OAuth2 refresh token", required = true)
    private final String refreshToken;

    public OAuthTokens() {
        this.accessToken = UUID.randomUUID().toString();
        this.refreshToken = UUID.randomUUID().toString();
    }

    public OAuthTokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}

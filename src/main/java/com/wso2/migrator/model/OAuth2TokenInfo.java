package com.wso2.migrator.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model of OAuth2 Access Token.
 */
public class OAuth2TokenInfo {

    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;
    @SerializedName("scope")
    private String scope;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("id_token")
    private String idToken;
    @SerializedName("expires_in")
    private long expiresIn;

    public String getAccessToken() {

        return accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public String getRefreshToken() {

        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {

        this.refreshToken = refreshToken;
    }

    public String getScope() {

        return scope;
    }

    public void setScope(String scope) {

        this.scope = scope;
    }

    public String getTokenType() {

        return tokenType;
    }

    public void setTokenType(String tokenType) {

        this.tokenType = tokenType;
    }

    public String getIdToken() {

        return idToken;
    }

    public void setIdToken(String idToken) {

        this.idToken = idToken;
    }

    public long getExpiresIn() {

        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {

        this.expiresIn = expiresIn;
    }
}
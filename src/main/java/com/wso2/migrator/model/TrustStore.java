package com.wso2.migrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrustStore {

    @JsonProperty("location")
    private String location;
    @JsonProperty("password")
    private String password = "wso2carbon";

    public String getLocation() {

        return location;
    }

    public void setLocation(String location) {

        this.location = location;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
}

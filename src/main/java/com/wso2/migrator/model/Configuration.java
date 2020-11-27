package com.wso2.migrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration {

    @JsonProperty("truststore")
    private TrustStore trustStore;

    @JsonProperty("environments")
    private Map<String, Environment> environmentMap = new HashMap<>();

    public Map<String, Environment> getEnvironmentMap() {

        return environmentMap;
    }

    public void setEnvironmentMap(Map<String, Environment> environmentMap) {

        this.environmentMap = environmentMap;
    }

    public TrustStore getTrustStore() {

        return trustStore;
    }

    public void setTrustStore(TrustStore trustStore) {

        this.trustStore = trustStore;
    }

}

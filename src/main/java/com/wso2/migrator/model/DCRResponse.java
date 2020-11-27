package com.wso2.migrator.model;

/**
 * DCR Response
 */
public class DCRResponse {

    private String callBackUrl;
    private String jsonString;
    private String clientId;
    private String clientSecret;

    public String getCallBackUrl() {

        return callBackUrl;
    }

    public String getJsonString() {

        return jsonString;
    }

    public void setJsonString(String jsonString) {

        this.jsonString = jsonString;
    }

    public String getClientId() {

        return clientId;
    }

    public void setClientId(String clientId) {

        this.clientId = clientId;
    }

    public String getClientSecret() {

        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {

        this.clientSecret = clientSecret;
    }
}

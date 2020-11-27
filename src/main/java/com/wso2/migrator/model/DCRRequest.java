package com.wso2.migrator.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * DCR Request
 */
public class DCRRequest {

    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "this is a model object ")
    private String callbackUrl = "";
    private String clientName;
    private String owner;
    private String grantType = "password refresh_token";
    private boolean saasApp = true;

    public DCRRequest(String clientName, String owner) {

        this.clientName = clientName;
        this.owner = owner;
    }


    public String getClientName() {

        return clientName;
    }

    public String getOwner() {

        return owner;
    }

    public String getGrantType() {

        return grantType;
    }

    public boolean isSaasApp() {

        return saasApp;
    }
}

package com.wso2.migrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class TenantDto {

    @JsonProperty("tenant-admin-user-name")
    private String tenantAdminUserName;
    @JsonProperty("tenant-admin-password")
    private String tenantAdminPassword;
    @JsonProperty("user-stores")
    private List<String> userstores = new ArrayList();

    public TenantDto() {

    }

    public String getTenantAdminUserName() {

        return tenantAdminUserName;
    }

    public void setTenantAdminUserName(String tenantAdminUserName) {

        this.tenantAdminUserName = tenantAdminUserName;
    }

    public String getTenantAdminPassword() {

        return tenantAdminPassword;
    }

    public void setTenantAdminPassword(String tenantAdminPassword) {

        this.tenantAdminPassword = tenantAdminPassword;
    }

    public List<String> getUserstores() {

        return userstores;
    }

    public void setUserstores(List<String> userstores) {

        this.userstores = userstores;
    }
}

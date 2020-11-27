package com.wso2.migrator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

public class Environment {

    @JsonProperty("registration_endpoint")
    private String dynamicClientRegistrationEndpoint;
    @JsonProperty("api_import_export_endpoint")
    private String apiImportExportEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("publisher-rest-api-endpoint")
    private String publisherRestAPIEndpoint;
    @JsonProperty("consumer-key")
    private String consumerKey;
    @JsonProperty("consumer-secret")
    private String consumerSecret;
    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;
    @JsonProperty("base_url")
    private String baseURl;
    @JsonProperty("admin-restapi-endpoint")
    private String adminRestAPIEndpoint;
    @JsonProperty("apimgtdb-url")
    private String apimgtDbUrl;
    @JsonProperty("apimgtdb-user")
    private String apimgtDbUser;
    @JsonProperty("apimgtdb-password")
    private String apimgtdbPassword;
    @JsonProperty("tenants")
    private Map<String, TenantDto> tenantDtoMap = new HashMap();

    public String getDynamicClientRegistrationEndpoint() {

        return dynamicClientRegistrationEndpoint;
    }

    public void setDynamicClientRegistrationEndpoint(String dynamicClientRegistrationEndpoint) {

        this.dynamicClientRegistrationEndpoint = dynamicClientRegistrationEndpoint;
    }

    public String getApiImportExportEndpoint() {

        return apiImportExportEndpoint;
    }

    public void setApiImportExportEndpoint(String apiImportExportEndpoint) {

        this.apiImportExportEndpoint = apiImportExportEndpoint;
    }

    public String getTokenEndpoint() {

        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {

        this.tokenEndpoint = tokenEndpoint;
    }

    public String getPublisherRestAPIEndpoint() {

        return publisherRestAPIEndpoint;
    }

    public String getConsumerKey() {

        return consumerKey;
    }

    public void setConsumerKey(String consumerKey) {

        this.consumerKey = consumerKey;
    }

    public String getConsumerSecret() {

        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {

        this.consumerSecret = consumerSecret;
    }

    public void setPublisherRestAPIEndpoint(String publisherRestAPIEndpoint) {

        this.publisherRestAPIEndpoint = publisherRestAPIEndpoint;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getBaseURl() {

        return baseURl;
    }

    public void setBaseURl(String baseURl) {

        this.baseURl = baseURl;
    }

    public String getAdminRestAPIEndpoint() {

        return adminRestAPIEndpoint;
    }

    public void setAdminRestAPIEndpoint(String adminRestAPIEndpoint) {

        this.adminRestAPIEndpoint = adminRestAPIEndpoint;
    }

    public void setApimgtDBURl(String apimgtDbUrl) {

        this.apimgtDbUrl = apimgtDbUrl;

    }

    public void setApimgtDBUsername(String apimgtDbUser) {

        this.apimgtDbUser = apimgtDbUser;

    }

    public void setApimgtDBPassword(String apimgtdbPassword) {

        this.apimgtdbPassword = apimgtdbPassword;
    }

    public String getApimgtDbUrl() {

        return apimgtDbUrl;
    }

    public String getApimgtDbUser() {

        return apimgtDbUser;
    }

    public String getApimgtdbPassword() {

        return apimgtdbPassword;
    }

    public void setApimgtDbUrl(String apimgtDbUrl) {

        this.apimgtDbUrl = apimgtDbUrl;
    }

    public void setApimgtDbUser(String apimgtDbUser) {

        this.apimgtDbUser = apimgtDbUser;
    }

    public void setApimgtdbPassword(String apimgtdbPassword) {

        this.apimgtdbPassword = apimgtdbPassword;
    }

    public Map<String, TenantDto> getTenantDtoMap() {

        return tenantDtoMap;
    }

    public void setTenantDtoMap(Map<String, TenantDto> tenantDtoMap) {

        this.tenantDtoMap = tenantDtoMap;
    }
}

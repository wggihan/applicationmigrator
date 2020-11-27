package com.wso2.migrator.interceptors;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class BearerHeaderInterceptor implements RequestInterceptor {

    private final String headerValue;

    public BearerHeaderInterceptor(String authToken) {

        this.headerValue = "Bearer ".concat(authToken);

    }

    public void apply(RequestTemplate template) {

        template.header("Authorization", headerValue);
    }
}

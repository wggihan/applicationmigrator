package com.wso2.migrator.invoker.general.serviceproxy;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

/**
 * Generate Token needed
 */
public interface Oauth2Service {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @RequestLine("POST /")
    Response generateAccessToken(@Param("grant_type") String grantType, @Param("username") String username,
                                 @Param("password") String password, @Param("scope") String scopes);
}

package com.wso2.migrator.invoker.general.serviceproxy;

import com.wso2.migrator.model.DCRRequest;
import feign.Headers;
import feign.RequestLine;
import feign.Response;

public interface DCRMService {

    @Headers("Content-Type: application/json")
    @RequestLine("POST /")
    public Response registerApplication(DCRRequest request);
}

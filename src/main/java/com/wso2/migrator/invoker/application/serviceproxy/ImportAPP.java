package com.wso2.migrator.invoker.application.serviceproxy;

import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;

import java.io.File;

public interface ImportAPP {


    @RequestLine("POST /import/applications?preserveOwner=true&update={update}&skipApplicationKeys={skipApplicationKeys}")
    @Headers("Content-Type: multipart/form-data")
    public Response importApp(@Param("file") File file, @Param("update") boolean update,
                              @Param("skipApplicationKeys") boolean skipApplicationKeys);

}

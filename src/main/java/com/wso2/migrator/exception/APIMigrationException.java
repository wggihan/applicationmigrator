package com.wso2.migrator.exception;

public class APIMigrationException extends Exception {

    public APIMigrationException(String message, Throwable cause) {

        super(message, cause);
    }

    public APIMigrationException(String message) {

        super(message);
    }

}

package com.wenglam.baking_app.exception;

import lombok.Getter;

@Getter
public class ExternalApiException extends RuntimeException {

    private final String provider;
    private final int statusCode;

    public ExternalApiException(String provider, int status, String message, Throwable cause) {
        super(message, cause);
        this.provider = provider;
        this.statusCode = status;
    }
}

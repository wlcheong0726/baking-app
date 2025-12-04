package com.wenglam.baking_app.external.tasty;

import com.wenglam.baking_app.exception.ApiNotSubscribedException;
import com.wenglam.baking_app.exception.ExternalApiException;
import com.wenglam.baking_app.exception.TastyRecipeNotFoundException;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TastyErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {

        switch (response.status()) {
            case 403:
                log.info("Method Key: {}. Response: {}", methodKey, response.toString());
                return new ApiNotSubscribedException("You are not subscribed to Tasty API: " + response.status());
            case 404:
                log.info("Method Key: {}. Response: {}", methodKey, response.toString());
                return new TastyRecipeNotFoundException("Tasty recipe not found: " + response.status());
            default:
                log.error("Method Key: {}. Response: {}", methodKey, response.toString());
                return new ExternalApiException(
                    "Tasty",
                    response.status(),
                    "Error calling Tasty API: (status code: " + response.status() + ")",
                    null
                );
        }
    }
}

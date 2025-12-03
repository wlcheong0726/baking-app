package com.wenglam.baking_app.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wenglam.baking_app.external.tasty.TastyErrorDecoder;

import feign.codec.ErrorDecoder;

@Configuration
public class TastyFeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new TastyErrorDecoder();
    }
}

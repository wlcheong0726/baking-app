package com.wenglam.baking_app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Configuration
@ConfigurationProperties(prefix = "tasty.api")
@Slf4j
@Data
public class TastyApiProperties {
    private String rapidapiHost;
    private String rapidapiKey;

    @PostConstruct
    public void init() {
        if (rapidapiHost == null || rapidapiHost.isEmpty()) {
            log.warn("TastyApiProperties: rapidApiHost is not set. Make sure to set it in production.");
        }

        if (rapidapiKey == null || rapidapiKey.isEmpty()) {
            log.warn("TastyApiProperties: rapidApiKey is not set. Make sure to set it in production.");
        }

        log.info("TastyApiProperties initialized with rapidApiHost: " + rapidapiHost + " and rapidApiKey.");
    }
 }

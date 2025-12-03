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
        log.info("TastyApiProperties initialized with rapidApiHost: " + rapidapiHost + " and rapidApiKey: " + rapidapiKey);
    }
 }

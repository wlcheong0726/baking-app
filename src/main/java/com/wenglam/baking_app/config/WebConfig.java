package com.wenglam.baking_app.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

public class WebConfig implements WebMvcConfigurer{

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String location = "file:" + System.getProperty("user.dir") + "/" + uploadDir + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location) // "file:uploads/"
                .setCachePeriod(3600); // Cache for 1 hour
    }
    
}

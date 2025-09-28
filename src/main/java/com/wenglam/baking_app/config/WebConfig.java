package com.wenglam.baking_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + System.getProperty("user.dir") + "/" + uploadDir + "/"; //System.getProperty("user.dir") = project root (JVM working directory)
        
        registry.addResourceHandler("/uploads/**") // Define the URL pattern to serve static files
                .addResourceLocations(location) // "file:$projectpath/uploads/"
                .setCachePeriod(3600); // Cache for 1 hour
    }
}
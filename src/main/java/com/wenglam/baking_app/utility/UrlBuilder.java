package com.wenglam.baking_app.utility;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class UrlBuilder {
    public static String buildFullUrl(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return relativePath;
        } else {
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(relativePath)
                    .toUriString();
        }
    }
}
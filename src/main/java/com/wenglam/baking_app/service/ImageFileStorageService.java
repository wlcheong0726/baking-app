package com.wenglam.baking_app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class ImageFileStorageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.public.base-url:http://localhost:8080}") // Base URL for constructing public URLs
    private String publicBaseUrl;

    @Value("${app.upload.allowed-types:image/jpeg,image/png}")
    private List<String> allowedTypes;

    private Path root;

    @PostConstruct
    public void init() throws IOException {
        // Resolves to an absolute filesystem path (project working directory by default)
        root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root); 
        System.out.println("[FileStorageService] [Upload] Saving files under " + root.toAbsolutePath());
    }

    /**
     * Saves file to disk and return a URL that the app will serve.
     * @throws HttpMediaTypeNotSupportedException 
     */
    public String store(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        // Check content type allowed
        String contentType = imageFile.getContentType();
        if (!isAllowedContentType(contentType)) {
            throw new IllegalArgumentException("Please upload JPEG or PNG files.");
        };

        String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename(); // Unique filename
        Path target = root.resolve(filename).normalize(); // Absolute path to the target file
        System.out.println("[FileStorageService]: " + root.resolve(filename).toString());
        System.out.println("[FileStorageService]: " + target.toString());

        try {
            Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING); // Save the file to disk
            System.out.println("[FileStorageService] Stored file " + filename + " at " + target.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }

        String relativeUrl = "/uploads/" + filename;
        if (publicBaseUrl == null || publicBaseUrl.isBlank()) {
            return relativeUrl;
        } 
            return publicBaseUrl.replaceAll("/$", "") + relativeUrl;
    }

    private boolean isAllowedContentType(String type) {
        if (type == null || allowedTypes == null) {
            return false;
        }

        return allowedTypes.stream()
            .map(String::trim)
            .anyMatch(allowedType -> allowedType.equalsIgnoreCase(type));
    }

    // TODO: Delete image on disk

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}

package com.wenglam.baking_app.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.public.base-url:http://localhost:8080}") // Base URL for constructing public URLs
    private String publicBaseUrl;

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
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String extension = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension); // Unique filename
        Path target = root.resolve(filename).normalize(); // Absolute path to the target file
        System.out.println("[FileStorageService]: " + root.resolve(filename).toString());
        System.out.println("[FileStorageService]: " + target.toString());

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING); // Save the file to disk
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

    private static String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return dot >= 0 ? filename.substring(dot + 1) : "";
    }
}

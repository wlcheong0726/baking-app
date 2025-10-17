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

import com.wenglam.baking_app.utility.ByteUtil;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageFileStorageService {
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.allowed-types:image/jpeg,image/png}")
    private List<String> allowedTypes;

    @Value("${spring.servlet.multipart.max-file-size:5MB}")
    private String maxFileSize;

    private Path root;

    @PostConstruct
    public void init() throws IOException {
        // Resolves to an absolute filesystem path (project working directory by default)
        root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(root); 
    }

    /**
     * Saves file to disk and return a URL that the app will serve.
     * @throws HttpMediaTypeNotSupportedException 
     */
    public String store(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;

        log.info("Image size={}B", imageFile.getSize());

        if (imageFile.getSize() > ByteUtil.parseSizeToBytes(maxFileSize)) {

            log.warn("Image size too large size={}, max image size={}", imageFile.getSize(), maxFileSize);
            throw new IllegalArgumentException("Image size too large size=" + imageFile.getSize() + ", max image size= " + maxFileSize);
        }

        // Check content type allowed
        String contentType = imageFile.getContentType();

        log.info("Image type={}.", contentType);

        if (!isAllowedContentType(contentType)) {
            log.warn("Rejected image type={}", contentType);
            throw new IllegalArgumentException("Rejected image type=" + contentType + "Please upload JPEG or PNG files.");
        };

        String filename = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename(); // Unique filename
        Path target = root.resolve(filename).normalize(); // Absolute path to the target file
        String relative = root.relativize(target).toString();
        log.info("Image saved path={}: ", relative);

        try {
            log.info("Storing image name={} type={} size={}B path={}", filename, imageFile.getContentType(), imageFile.getSize(), relative);
            Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING); // Save the file to disk
        } catch (IOException e) {
            log.error("Failed to store image name={} type={} size={}B path={}", filename, imageFile.getContentType(), imageFile.getSize(), relative);
            throw new IllegalStateException("Failed to store image", e);
            // TODO - review type of exception thrown
        }

        String relativeUrl = "/uploads/" + filename;
        
        return relativeUrl;
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

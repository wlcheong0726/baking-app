package com.wenglam.baking_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.wenglam.baking_app.service.ImageFileStorageService;

@ExtendWith(MockitoExtension.class)
public class ImageFileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    MultipartFile multipartFileMock;

    @InjectMocks
    private ImageFileStorageService imageFileStorageService;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(imageFileStorageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(imageFileStorageService, "publicBaseUrl", "http://localhost:8080");
        ReflectionTestUtils.setField(imageFileStorageService, "allowedTypes", List.of("image/jpeg", "image/png"));
        ReflectionTestUtils.setField(imageFileStorageService, "maxFileSize", "5242880"); // 5MB in bytes

        imageFileStorageService.init();
    }


    @Test
    void store_ValidImage_Returns_fullImageUrl() throws IOException {
        when(multipartFileMock.isEmpty()).thenReturn(false);
        when(multipartFileMock.getSize()).thenReturn(1024L);
        when(multipartFileMock.getContentType()).thenReturn("image/png");
        when(multipartFileMock.getOriginalFilename()).thenReturn("test_image.png");
        when(multipartFileMock.getInputStream()).thenReturn(new ByteArrayInputStream("fake image".getBytes()));

        String imageUrl = imageFileStorageService.store(multipartFileMock);

        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("http://localhost:8080/uploads/"));
        assertTrue(imageUrl.endsWith("_test_image.png"));
    }

    @Test
    void store_ValidImage_Returns_relativeImageUrl() throws IOException {
        ReflectionTestUtils.setField(imageFileStorageService, "publicBaseUrl", null);
        when(multipartFileMock.isEmpty()).thenReturn(false);
        when(multipartFileMock.getSize()).thenReturn(1024L);
        when(multipartFileMock.getContentType()).thenReturn("image/png");
        when(multipartFileMock.getOriginalFilename()).thenReturn("test_image.png");
        when(multipartFileMock.getInputStream()).thenReturn(new ByteArrayInputStream("fake image".getBytes()));

        String imageUrl = imageFileStorageService.store(multipartFileMock);

        assertNotNull(imageUrl);
        assertTrue(imageUrl.startsWith("/uploads/"));
        assertTrue(imageUrl.endsWith("_test_image.png"));
    }

    @Test
    void store_ImageSizeTooLarge_Throws_IllegalArgumentException() throws IOException {
        when(multipartFileMock.isEmpty()).thenReturn(false);
        when(multipartFileMock.getSize()).thenReturn(100000000L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> imageFileStorageService.store(multipartFileMock));
        assertTrue(exception.getMessage().contains("Image size too large"));
    }

    @Test
    void store_InvalidContentType_Throws_IllegalArgumentException() throws IOException {
        when(multipartFileMock.isEmpty()).thenReturn(false);
        when(multipartFileMock.getSize()).thenReturn(1024L);
        when(multipartFileMock.getContentType()).thenReturn("application/x-7z-compressed");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> imageFileStorageService.store(multipartFileMock));
        assertTrue(exception.getMessage().contains("Rejected image type"));
    }

    @Test
    void store_ValidImage_Throws_IllegalStateException() throws IOException {
        when(multipartFileMock.isEmpty()).thenReturn(false);
        when(multipartFileMock.getSize()).thenReturn(1024L);
        when(multipartFileMock.getContentType()).thenReturn("image/png");
        when(multipartFileMock.getOriginalFilename()).thenReturn("test_image.png");
        when(multipartFileMock.getInputStream()).thenThrow(new IOException());

        Exception exception = assertThrows(IllegalStateException.class, () -> imageFileStorageService.store(multipartFileMock));
        assertTrue(exception.getMessage().contains("Failed to store image"));
    }
}
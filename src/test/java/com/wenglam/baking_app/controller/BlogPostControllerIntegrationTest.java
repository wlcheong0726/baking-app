package com.wenglam.baking_app.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.wenglam.baking_app.entity.BlogPost;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// properties = {"server.port=8081","hostname=192.168.0.2"})
// @TestPropertySource(locations = "/application-test.properties",
// properties = {"server.port=8088"})
public class BlogPostControllerIntegrationTest {
    
    @Value("${server.port}")
    private int serverPort; // port number from application.properties - used in production environment

    @LocalServerPort
    private int localServerPort; // actual port number used in the test

    @Value("${hostname:localhost}")
    private String hostName;

    @Value("${public.base.url:http://localhost:8080}")
    private String publicBaseUrl;
    // TODO: ^^^to change later so it uses random port number in tests after ImageFileStorageService has been updated to enable using random port num

    // @PostConstruct // Runs after the constructor and dependency injection
    // void init() {
    //     if (localServerPort != 0) {
    //         publicBaseUrl = "https://" + hostName + ":" + localServerPort;
    //     } else {
    //         publicBaseUrl = "http://" + hostName + ":" + serverPort;
    //     }
    // }

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetAllBlogPosts() {
        
    }

    @Test
    void testCreateBlogPost_whenValidInputAndNoImage_thenReturns201() {
        // Given
        // JSONObject blogPostRequestJSON = new JSONObject();
        // blogPostRequestJSON.put("title", "Integration Test Title");
        // blogPostRequestJSON.put("content", "Integration Test Content");
        // blogPostRequestJSON.put("author", "Integration Test Author");

        // System.out.println("Blog Post Request JSON: " + blogPostRequestJSON.toJSONString());

        MultiValueMap<String, Object> blogPostRequestData = new LinkedMultiValueMap<String,Object>();
        blogPostRequestData.add("title", "Integration Test Title - Cheesecake!");
        blogPostRequestData.add("author", "Integration Test Author");
        blogPostRequestData.add("content", "Integration Test Content. This is content. More contents.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(blogPostRequestData, headers);

        // When
        ResponseEntity<BlogPost> response = testRestTemplate.postForEntity("/api/v1/blogposts", request, BlogPost.class);
        BlogPost createdBlogPost = response.getBody();

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        if (createdBlogPost != null) {
            assertNotNull(createdBlogPost.getId(), "Blog Post ID should not be null");
            assertTrue(createdBlogPost.getId() > 0, "Blog Post ID should be greater than 0");
            assertEquals(createdBlogPost.getTitle(), blogPostRequestData.getFirst("title"), "Title should match");
            assertEquals(createdBlogPost.getAuthor(), blogPostRequestData.getFirst("author"), "Author should match");
            assertEquals(createdBlogPost.getContent(), blogPostRequestData.getFirst("content"), "Content should match");
        }
    }

    @Test
    void testCreateBlogPost_whenValidInputAndWithImage_thenReturns201() {
        // Given
        MultiValueMap<String, Object> blogPostRequestData = new LinkedMultiValueMap<String,Object>();
        blogPostRequestData.add("title", "Integration Test Title - Cheesecake!");
        blogPostRequestData.add("author", "Integration Test Author");
        blogPostRequestData.add("content", "Integration Test Content. This is content. More contents.");

        ClassPathResource imageResource = new ClassPathResource("testData/Burnt-Basque-cheesecake.jpg");

        // Alternative way to load image from file system
        // FileSystemResource fileResource = new FileSystemResource("/Users/yourname/Desktop/test-image.jpg");

        // Alternative way to create a ByteArrayResource which doesn't rely on external file
        // byte[] randomBytes = new byte[1024]; // 1KB random data
        // new Random().nextBytes(randomBytes);

        // ByteArrayResource byteArrayResource = new ByteArrayResource(randomBytes) {
        //     @Override
        //     public String getFilename() {
        //         return "random-image.jpg";
        //     }
        // };

        blogPostRequestData.add("imageFile", imageResource);
        

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(blogPostRequestData, headers);

        // When
        ResponseEntity<BlogPost> response = testRestTemplate.postForEntity("/api/v1/blogposts", request, BlogPost.class);
        BlogPost createdBlogPost = response.getBody();

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        if (createdBlogPost != null) {
            assertNotNull(createdBlogPost.getId(), "Blog Post ID should not be null");
            assertTrue(createdBlogPost.getId() > 0, "Blog Post ID should be greater than 0");
            assertEquals(createdBlogPost.getTitle(), blogPostRequestData.getFirst("title"), "Title should match");
            assertEquals(createdBlogPost.getAuthor(), blogPostRequestData.getFirst("author"), "Author should match");
            assertEquals(createdBlogPost.getContent(), blogPostRequestData.getFirst("content"), "Content should match");
            assertNotNull(createdBlogPost.getImageUrl(), "Image URL should not be null");
            assertTrue(createdBlogPost.getImageUrl().startsWith(publicBaseUrl + "/uploads/"), "Image URL should contain the uploads path at the start");
            assertTrue(createdBlogPost.getImageUrl().endsWith(imageResource.getFilename()), "Image URL should contain the file name at the end");
        }
    }
}

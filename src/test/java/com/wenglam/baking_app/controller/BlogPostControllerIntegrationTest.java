package com.wenglam.baking_app.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestPropertySource(locations = "/application-test.properties")
public class BlogPostControllerIntegrationTest {
    
    @Value("${server.port}")
    private int serverPort; // port number from application.properties - used in production environment

    @LocalServerPort
    private int localServerPort; // actual port number used in the test

    @Value("localhost")
    private String hostName;

    private String publicBaseUrl;

    @TempDir
    private Path tempDir;

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeAll
    void setUp() throws IOException {
        publicBaseUrl = "http://" + hostName + ":" + localServerPort;
    }

    @BeforeEach
    void cleanDatabase() {
        blogPostRepository.deleteAll();
    }
    
    private BlogPost seedBlogPostWithImageInDatabase() {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("Integration Test Title - Cheesecake!");
        blogPost.setAuthor("Integration Test Author");
        blogPost.setContent("Integration Test Content. This is content. More contents.");
        UUID uuid = UUID.randomUUID();
        blogPost.setImageUrl(publicBaseUrl + "/uploads/" + uuid + "test-image.jpg");

        return blogPostRepository.saveAndFlush(blogPost);
    }

    private BlogPost seedBlogPostWithoutImageInDatabase() {
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle("Integration Test Title - Cheesecake!");
        blogPost.setAuthor("Integration Test Author");
        blogPost.setContent("Integration Test Content. This is content. More contents.");

        return blogPostRepository.saveAndFlush(blogPost);
    }

    @Test
    void contextLoads() {
        System.out.println("local server port: " + localServerPort);
        System.out.println("public base URL: " + publicBaseUrl);
        System.out.println("tempDir: " + tempDir.toString());

    }

    @Nested
    class CreateBlogPostTests {
        @Test
        void testCreateBlogPost_whenValidInputAndNoImage_thenReturns201() {
            // Given
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
                System.out.println("Created Blog Post ID: " + createdBlogPost.getId());
                assertTrue(createdBlogPost.getId() > 0, "Blog Post ID should be greater than 0");
                assertEquals(createdBlogPost.getTitle(), blogPostRequestData.getFirst("title"), "Title should match");
                assertEquals(createdBlogPost.getAuthor(), blogPostRequestData.getFirst("author"), "Author should match");
                assertEquals(createdBlogPost.getContent(), blogPostRequestData.getFirst("content"), "Content should match");
                assertNotNull(createdBlogPost.getImageUrl(), "Image URL should not be null");
                System.out.println("publicBaseUrl: " + publicBaseUrl);
                System.out.println("crated image url: " + createdBlogPost.getImageUrl());
                assertTrue(createdBlogPost.getImageUrl().startsWith(publicBaseUrl + "/uploads/"), "Image URL should contain the uploads path at the start");
                assertTrue(createdBlogPost.getImageUrl().endsWith(imageResource.getFilename()), "Image URL should contain the file name at the end");
            }
        }
    }

    @Nested
    class GetBlogPostsTests {
        @Test
        void testGetBlogPostById_whenBlogPostExists_thenReturns200() {
            // Given
            System.out.println("testGetBlogPostById_whenBlogPostExists_thenReturns200");
            BlogPost seededBlogPost = seedBlogPostWithImageInDatabase();
            List<BlogPost> allPosts = blogPostRepository.findAll();
            System.out.println("No. of blog posts in DB: " + allPosts.size());
            System.out.println(allPosts.get(0).toString());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON));

            // When
            ResponseEntity<BlogPost> response = testRestTemplate.getForEntity("/api/v1/blogposts/blogpost/{id}", BlogPost.class, seededBlogPost.getId());
            System.out.println("Fetching blog post with ID: " + seededBlogPost.getId());
            BlogPost fetchedBlogPost = response.getBody();

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            if (fetchedBlogPost != null) {
                assertNotNull(fetchedBlogPost.getId(), "Blog Post ID should not be null");
                assertTrue(fetchedBlogPost.getId() > 0, "Blog Post ID should be greater than 0");
                assertEquals(fetchedBlogPost.getTitle(), seededBlogPost.getTitle(), "Title should match");
                assertEquals(fetchedBlogPost.getAuthor(), seededBlogPost.getAuthor(), "Author should match");
                assertEquals(fetchedBlogPost.getContent(), seededBlogPost.getContent(), "Content should match");
                assertNotNull(fetchedBlogPost.getImageUrl(), "Image URL should not be null");
                System.out.println("publicBaseUrl: " + publicBaseUrl);
                System.out.println("crated image url: " + seededBlogPost.getImageUrl());
                assertTrue(fetchedBlogPost.getImageUrl().startsWith(publicBaseUrl + "/uploads/"), "Image URL should contain the uploads path at the start");
                assertTrue(fetchedBlogPost.getImageUrl().endsWith("test-image.jpg"), "Image URL should contain the file name at the end");}
        }

        @Test
        void testGetBlogPostById_whenBlogPostNotExist_thenReturns404() {
            System.out.println("testGetBlogPostById_whenBlogPostNotExists_thenReturns404");

            // Given
            seedBlogPostWithImageInDatabase();
            System.out.println("db size after seeding: " + blogPostRepository.count());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON));

            // When
            ResponseEntity<BlogPost> response = testRestTemplate.getForEntity("/api/v1/blogposts/blogpost/{id}", BlogPost.class, 99L);

            // Then
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class UpdateBlogPostTests {
        @Test
        void testUpdatePost_whenValidInputWithSameImage_thenReturns200() {

        }

        @Test
        void testUpdatePost_whenValidInputWithDifferentImage_thenReturns200() {

        }

        @Test
        void testUpdatePost_whenValidInputWithImageRemoved_thenReturns200() {
            
        }

        @Test
        void testUpdatePost_whenValidInputWithImageAdded_thenReturns200() {
            
        }
    }

    @Nested
    class DeleteBlogPostTests {
        @Test
        void testDeleteBlogPost_whenBlogPostExists_thenReturns204() {

        }

        @Test
        void testDeleteBlogPost_whenBlogPostNotExist_thenReturns404() {
            
        }
    }
}

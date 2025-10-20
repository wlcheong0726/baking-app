package com.wenglam.baking_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;
import com.wenglam.baking_app.service.ImageFileStorageService;
import com.wenglam.baking_app.service.impl.BlogPostService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class BlogPostServiceTest {

    @Mock
    private BlogPostRepository blogPostRepositoryMock;

    @Mock
    private MultipartFile multipartFileMock;

    @Mock
    private ImageFileStorageService imageFileStorageServiceMock;

    @InjectMocks // create blogPostService and inject blogPostRepositoryMock into it
    private BlogPostService blogPostService;

    private BlogPost blogPost;

    @BeforeEach
    void setUp() {
        blogPost = new BlogPost();
        blogPost.setId(1L);
        blogPost.setTitle("Test Title");
        blogPost.setContent("Test Content");
        blogPost.setAuthor("Test Author");
        blogPost.setImageUrl("http://localhost:8080/uploads/image.png");
        blogPost.setCreatedAt(Instant.now());
    }

    @Nested
    class CreateBlogTests {
        @Test
        void createBlogPostWithImage_Success() {
            /**
             * test that created blog post contains same data as the input blog post
             * test that the created blog post is not null
             * test that the created blog post has an ID (indicating it was saved)
             * test that the repository's save method was called once
             */
            BlogPostCreateData blogPostCreateData = new BlogPostCreateData();
            blogPostCreateData.setTitle("Test Title");
            blogPostCreateData.setAuthor("Test Author");
            blogPostCreateData.setContent("Test Content");
            blogPostCreateData.setImageFile(multipartFileMock);

            String imageUrl = "http://localhost:8080/uploads/image.png";

            BlogPost savedBlogPost = new BlogPost();
            savedBlogPost.setTitle(blogPostCreateData.getTitle());
            savedBlogPost.setContent(blogPostCreateData.getContent());
            savedBlogPost.setAuthor(blogPostCreateData.getAuthor());
            savedBlogPost.setImageUrl(imageUrl);
            savedBlogPost.setCreatedAt(Instant.now());

            when(imageFileStorageServiceMock.store(blogPostCreateData.getImageFile())).thenReturn(imageUrl);

            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost createdBlogPost = blogPostService.createBlogPost(blogPostCreateData);

            assertAll(
                    () -> assertEquals(createdBlogPost.getTitle(), savedBlogPost.getTitle()),
                    () -> assertEquals(createdBlogPost.getContent(), savedBlogPost.getContent()),
                    () -> assertEquals(createdBlogPost.getAuthor(), savedBlogPost.getAuthor()),
                    () -> assertEquals(createdBlogPost.getImageUrl(), savedBlogPost.getImageUrl()),
                    () -> assertNotNull(savedBlogPost.getCreatedAt()),
                    () -> assertNull(savedBlogPost.getUpdatedAt()),
                    () -> assertNull(savedBlogPost.getUpdatedBy()));
            assertNotNull(savedBlogPost);

            verify(imageFileStorageServiceMock, times(1)).store(blogPostCreateData.getImageFile());
            verify(blogPostRepositoryMock, times(1)).save(any(BlogPost.class));
        }

        @Test
        void createBlogPostWithoutImage_Success() {
            BlogPostCreateData blogPostCreateData = new BlogPostCreateData();
            blogPostCreateData.setTitle("Test Title");
            blogPostCreateData.setAuthor("Test Author");
            blogPostCreateData.setContent("Test Content");

            BlogPost savedBlogPost = new BlogPost();
            savedBlogPost.setTitle(blogPostCreateData.getTitle());
            savedBlogPost.setContent(blogPostCreateData.getContent());
            savedBlogPost.setAuthor(blogPostCreateData.getAuthor());
            savedBlogPost.setCreatedAt(Instant.now());

            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost createdBlogPost = blogPostService.createBlogPost(blogPostCreateData);

            assertAll(
                    () -> assertEquals(createdBlogPost.getTitle(), savedBlogPost.getTitle()),
                    () -> assertEquals(createdBlogPost.getContent(), savedBlogPost.getContent()),
                    () -> assertEquals(createdBlogPost.getAuthor(), savedBlogPost.getAuthor()),
                    () -> assertNull(savedBlogPost.getImageUrl()),
                    () -> assertNotNull(savedBlogPost.getCreatedAt()),
                    () -> assertNull(savedBlogPost.getUpdatedAt()),
                    () -> assertNull(savedBlogPost.getUpdatedBy()));
            assertNotNull(savedBlogPost);

            verify(imageFileStorageServiceMock, never()).store(any());
            verify(blogPostRepositoryMock, times(1)).save(any(BlogPost.class));
        }

        @Test
        void createBlogPostWithImage_Fail() {
            BlogPostCreateData blogPostCreateData = new BlogPostCreateData();
            blogPostCreateData.setTitle("Test Title");
            blogPostCreateData.setAuthor("Test Author");
            blogPostCreateData.setContent("Test Content");
            blogPostCreateData.setImageFile(multipartFileMock);

            when(imageFileStorageServiceMock.store(blogPostCreateData.getImageFile())).thenThrow(new IllegalArgumentException());

            assertThrows(IllegalArgumentException.class, () -> blogPostService.createBlogPost(blogPostCreateData));
            verify(blogPostRepositoryMock, never()).save(any());
        }
    }

    @Nested
    class getAllBlogPosts {
        @Test
        void getAllBlogPosts_Success() {
            // Given
            blogPost = new BlogPost();
            blogPost.setId(1L);
            blogPost.setTitle("Test Title");
            blogPost.setContent("Test Content");
            blogPost.setAuthor("Test Author");
            blogPost.setCreatedAt(Instant.now());

            BlogPost blogPost2 = new BlogPost();
            blogPost2.setId(2L);
            blogPost2.setTitle("Test Title2");
            blogPost2.setContent("Test Content2");
            blogPost2.setAuthor("Test Author2");
            blogPost2.setCreatedAt(Instant.now());
            blogPost2.setUpdatedAt(Instant.now());
            blogPost2.setUpdatedBy(blogPost2.getAuthor());

            List<BlogPost> blogPosts = List.of(blogPost, blogPost2);
            when(blogPostRepositoryMock.findAll()).thenReturn(blogPosts);

            List<BlogPost> foundBlogPosts = blogPostService.getAllBlogPosts();

            assertEquals(blogPosts.size(), foundBlogPosts.size());
            assertEquals(blogPosts, foundBlogPosts);
            assertEquals("Test Author", foundBlogPosts.get(0).getAuthor());
            assertEquals("Test Content2", foundBlogPosts.get(1).getContent());
            assertNotNull(foundBlogPosts);
            verify(blogPostRepositoryMock, times(1)).findAll();
        }

        @Test
        void getAllBlogPosts_EmptyList() {
            when(blogPostRepositoryMock.findAll()).thenReturn(List.of());

            List<BlogPost> foundBlogPosts = blogPostService.getAllBlogPosts();

            assertTrue(foundBlogPosts.isEmpty());
            verify(blogPostRepositoryMock, times(1)).findAll();
        }
    }

    @Nested
    class getBlogPostById {
        @Test
        void getBlogPostById_Success() {
            when(blogPostRepositoryMock.existsById(1L)).thenReturn(true);
            when(blogPostRepositoryMock.findById(1L)).thenReturn(Optional.of(blogPost));

            BlogPost foundBlogPost = blogPostService.getBlogPostById(1L);

            assertDoesNotThrow(() -> blogPostService.getBlogPostById(1L));
            assertEquals(blogPost, foundBlogPost);
        }

        @Test
        void getBlogPostById_throwsException() {
            when(blogPostRepositoryMock.existsById(99L)).thenReturn(false);

            Exception exception = assertThrows(EntityNotFoundException.class,
                    () -> {
                        blogPostService.getBlogPostById(99L);
                    });

            assertTrue(exception.getMessage().contains("99")
                    && exception.getMessage().contains("Unable to find blog post"));
            verify(blogPostRepositoryMock, never()).findById(anyLong());
            verifyNoMoreInteractions(blogPostRepositoryMock);
        }
    }

    @Nested
    class UpdateBlogPost {
        @Test
        void updateBlogPostWithImage_WithImageUpdate() {
            BlogPostUpdateData blogPostUpdateData = new BlogPostUpdateData();
            blogPostUpdateData.setTitle("Updated Title");
            blogPostUpdateData.setContent("Updated Content");
            blogPostUpdateData.setAuthor("Updated Author");
            blogPostUpdateData.setImageFile(multipartFileMock);

            String ImageUrlToBeUpdated = "http://localhost:8080/uploads/updated_image.png";

            when(blogPostRepositoryMock.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
            when(imageFileStorageServiceMock.store(multipartFileMock)).thenReturn(ImageUrlToBeUpdated);
            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost updatedBlogPost = blogPostService.updateBlogPost(blogPost.getId(), blogPostUpdateData);

            assertAll(
                    () -> assertEquals(blogPostUpdateData.getTitle(), updatedBlogPost.getTitle()),
                    () -> assertEquals(blogPostUpdateData.getContent(), updatedBlogPost.getContent()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getAuthor()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getUpdatedBy()),
                    () -> assertEquals(ImageUrlToBeUpdated, updatedBlogPost.getImageUrl()),
                    () -> assertNotNull(updatedBlogPost.getUpdatedAt()),
                    () -> assertEquals(blogPost.getCreatedAt(), updatedBlogPost.getCreatedAt()));
            verify(blogPostRepositoryMock, times(1)).findById(blogPost.getId());
            verify(imageFileStorageServiceMock, times(1)).store(multipartFileMock);
            verify(blogPostRepositoryMock, times(1)).save(updatedBlogPost);
        }

        @Test
        void updateBlogPostWithImage_NoImageUpdate() {
            BlogPostUpdateData blogPostUpdateData = new BlogPostUpdateData();
            blogPostUpdateData.setTitle("Updated Title");
            blogPostUpdateData.setContent("Updated Content");
            blogPostUpdateData.setAuthor("Updated Author");
            blogPostUpdateData.setImageUrl("http://localhost:8080/uploads/image.png");

            when(blogPostRepositoryMock.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost updatedBlogPost = blogPostService.updateBlogPost(blogPost.getId(), blogPostUpdateData);

            assertAll(
                    () -> assertEquals(blogPostUpdateData.getTitle(), updatedBlogPost.getTitle()),
                    () -> assertEquals(blogPostUpdateData.getContent(), updatedBlogPost.getContent()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getAuthor()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getUpdatedBy()),
                    () -> assertEquals(blogPostUpdateData.getImageUrl(), updatedBlogPost.getImageUrl()),
                    () -> assertNotNull(updatedBlogPost.getUpdatedAt()),
                    () -> assertEquals(blogPost.getCreatedAt(), updatedBlogPost.getCreatedAt())
            );
            verify(blogPostRepositoryMock, times(1)).findById(blogPost.getId());
            verify(imageFileStorageServiceMock, never()).store(any());
            verify(blogPostRepositoryMock, times(1)).save(updatedBlogPost);
        }

        @Test
        void updateBlogPostWithoutImage_ImageAdded() {
            BlogPost existingBlogPost = new BlogPost();
            existingBlogPost.setId(1L);
            existingBlogPost.setTitle("Test Title");
            existingBlogPost.setContent("Test Content");
            existingBlogPost.setAuthor("Test Author");
            existingBlogPost.setCreatedAt(Instant.now());

            BlogPostUpdateData blogPostUpdateData = new BlogPostUpdateData();
            blogPostUpdateData.setTitle("Updated Title");
            blogPostUpdateData.setContent("Updated Content");
            blogPostUpdateData.setAuthor("Updated Author");
            blogPostUpdateData.setImageFile(multipartFileMock);

            String imageUrl = "http://localhost:8080/uploads/image.png";

            when(blogPostRepositoryMock.findById(existingBlogPost.getId())).thenReturn(Optional.of(existingBlogPost));
            when(imageFileStorageServiceMock.store(any())).thenReturn(imageUrl);
            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost updatedBlogPost = blogPostService.updateBlogPost(existingBlogPost.getId(), blogPostUpdateData);

            assertAll(
                    () -> assertEquals(blogPostUpdateData.getTitle(), updatedBlogPost.getTitle()),
                    () -> assertEquals(blogPostUpdateData.getContent(), updatedBlogPost.getContent()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getAuthor()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getUpdatedBy()),
                    () -> assertEquals(imageUrl, updatedBlogPost.getImageUrl()),
                    () -> assertNotNull(updatedBlogPost.getUpdatedAt()),
                    () -> assertEquals(existingBlogPost.getCreatedAt(), updatedBlogPost.getCreatedAt())
            );
            verify(blogPostRepositoryMock, times(1)).findById(existingBlogPost.getId());
            verify(imageFileStorageServiceMock, times(1)).store(multipartFileMock);
            verify(blogPostRepositoryMock, times(1)).save(updatedBlogPost);
        }

        @Test
        void updateBlogPostWithoutImage_NoImageUpdate() {
            BlogPostUpdateData blogPostUpdateData = new BlogPostUpdateData();
            blogPostUpdateData.setTitle("Updated Title");
            blogPostUpdateData.setContent("Updated Content");
            blogPostUpdateData.setAuthor("Updated Author");

            when(blogPostRepositoryMock.findById(blogPost.getId())).thenReturn(Optional.of(blogPost));
            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost updatedBlogPost = blogPostService.updateBlogPost(blogPost.getId(), blogPostUpdateData);

            assertAll(
                    () -> assertEquals(blogPostUpdateData.getTitle(), updatedBlogPost.getTitle()),
                    () -> assertEquals(blogPostUpdateData.getContent(), updatedBlogPost.getContent()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getAuthor()),
                    () -> assertEquals(blogPostUpdateData.getAuthor(), updatedBlogPost.getUpdatedBy()),
                    () -> assertEquals(blogPostUpdateData.getImageUrl(), updatedBlogPost.getImageUrl()),
                    () -> assertNotNull(updatedBlogPost.getUpdatedAt()),
                    () -> assertEquals(blogPost.getCreatedAt(), updatedBlogPost.getCreatedAt())
            );
            verify(blogPostRepositoryMock, times(1)).findById(blogPost.getId());
            verify(imageFileStorageServiceMock, never()).store(any());
            verify(blogPostRepositoryMock, times(1)).save(updatedBlogPost);
        }

        @Test
        void testUpdateBlogPost_throwsException() {
            BlogPostUpdateData blogPostUpdateData = new BlogPostUpdateData();
            blogPostUpdateData.setTitle("Updated Title");
            blogPostUpdateData.setContent("Updated Content");
            blogPostUpdateData.setAuthor("Updated Author");
            blogPostUpdateData.setImageUrl("http://localhost:8080/uploads/image.png");

            when(blogPostRepositoryMock.findById(99L)).thenReturn(Optional.empty());

            Exception exception = assertThrows(EntityNotFoundException.class,
                    () -> {
                        blogPostService.updateBlogPost(99L, blogPostUpdateData);
                    });

            assertTrue(exception.getMessage().contains("99") && exception.getMessage().contains("not found"));
            verify(blogPostRepositoryMock, times(1)).findById(99L);
            verify(blogPostRepositoryMock, never()).save(any(BlogPost.class));
            verifyNoMoreInteractions(blogPostRepositoryMock);
        }
    }

    @Nested
    class DeleteBlogPost {
        @Test
        void deleteBlogPost_Success() {
            when(blogPostRepositoryMock.existsById(1L)).thenReturn(true);

            assertDoesNotThrow(() -> blogPostService.deleteBlogPost(1L));

            verify(blogPostRepositoryMock, times(1)).existsById(1L);
            verify(blogPostRepositoryMock, times(1)).deleteById(1L);
            verifyNoMoreInteractions(blogPostRepositoryMock);
        }

        @Test
        void deleteBlogPost_NotFound() {
            when(blogPostRepositoryMock.existsById(99L)).thenReturn(false);

            assertThrows(EntityNotFoundException.class, () -> blogPostService.deleteBlogPost(99L));
            verify(blogPostRepositoryMock, times(1)).existsById(99L);
            verify(blogPostRepositoryMock, never()).deleteById(anyLong());
        }
    }
}
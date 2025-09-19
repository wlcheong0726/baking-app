package com.wenglam.baking_app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.mockito.*;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;
import com.wenglam.baking_app.service.impl.BlogPostService;

import jakarta.persistence.EntityNotFoundException;

public class BlogPostServiceTest {
    
    @Mock
    private BlogPostRepository blogPostRepositoryMock;

    @InjectMocks // create blogPostService and inject blogPostRepositoryMock into it
    private BlogPostService blogPostService;

    private BlogPost blogPost;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        blogPost = new BlogPost();                             
        blogPost.setId(1L);
        blogPost.setTitle("Test Title");
        blogPost.setContent("Test Content");
        blogPost.setAuthor("Test Author");
    }

    // Create blog post

    @Test
    void testCreateBlogPost() {
        /**
         * test that created blog post contains same data as the input blog post
         * test that the created blog post is not null
         * test that the created blog post has an ID (indicating it was saved)
         * test that the repository's save method was called once
         */

        when(blogPostRepositoryMock.save(blogPost)).thenReturn(blogPost);

        BlogPost createdBlogPost = blogPostService.createBlogPost(blogPost);

        assertAll(
            () -> assertEquals(createdBlogPost.getId(), blogPost.getId()),
            () -> assertEquals(createdBlogPost.getTitle(), blogPost.getTitle()),
            () -> assertEquals(createdBlogPost.getContent(), blogPost.getContent()),
            () -> assertEquals(createdBlogPost.getAuthor(), blogPost.getAuthor())
        );
        assertNotNull(createdBlogPost);
        verify(blogPostRepositoryMock, times(1)).save(blogPost);
    }

    // Get all blog posts

    @Test
    void testGetAllBlogPosts() {
        // Given
        BlogPost blogPost2 = new BlogPost();                             
        blogPost.setId(2L);
        blogPost.setTitle("Test Title2");
        blogPost.setContent("Test Content2");
        blogPost.setAuthor("Test Author2");

        List<BlogPost> blogPosts = List.of(blogPost, blogPost2);
        when(blogPostRepositoryMock.findAll()).thenReturn(blogPosts);

        List<BlogPost> foundBlogPosts = blogPostService.getAllBlogPosts();

        assertEquals(2, foundBlogPosts.size());
        assertEquals(blogPosts, foundBlogPosts);
        assertEquals("Test Author", foundBlogPosts.get(0).getAuthor());
        assertEquals("Test Content2", foundBlogPosts.get(1).getContent());
        assertNotNull(foundBlogPosts);
        verify(blogPostRepositoryMock, times(1)).findAll();
    }

    @Test
    void testGetAllBlogPosts_EmptyList() {
        when(blogPostRepositoryMock.findAll()).thenReturn(List.of());

        List<BlogPost> foundBlogPosts = blogPostService.getAllBlogPosts();

        assertTrue(foundBlogPosts.isEmpty());
        verify(blogPostRepositoryMock, times(1)).findAll();
    }

    // Delete blog post

    @Test
    void testDeleteBlogPost_Success() {
        when(blogPostRepositoryMock.existsById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> blogPostService.deleteBlogPost(1L));

        verify(blogPostRepositoryMock, times(1)).existsById(1L);
        verify(blogPostRepositoryMock, times(1)).deleteById(1L);
        verifyNoMoreInteractions(blogPostRepositoryMock);
    }

    @Test
    void testDeleteBlogPost_NotFound() {
        when(blogPostRepositoryMock.existsById(99L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> blogPostService.deleteBlogPost(99L));
        verify(blogPostRepositoryMock, times(1)).existsById(99L);
        verify(blogPostRepositoryMock, never()).deleteById(anyLong());
    }

    // Get blog post by ID

    @Test
    void testGetBlogPostById() {
        when(blogPostRepositoryMock.findById(1L)).thenReturn(Optional.of(blogPost));

        BlogPost foundBlogPost = blogPostService.getBlogPostById(1L);

        assertEquals(blogPost, foundBlogPost);
    }

    @Test
    void testGetBlogPostById_throwsException() {
        when(blogPostRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, 
            () -> {
                blogPostService.getBlogPostById(99L);
            }
        );

        assertTrue(exception.getMessage().contains("99") && exception.getMessage().contains("not found"));
        verify(blogPostRepositoryMock, times(1)).findById(99L);
        verifyNoMoreInteractions(blogPostRepositoryMock);
    }

    // Update blog post

    @Test
    void testUpdateBlogPost() {
        BlogPost existingBlogPost = blogPost;
        BlogPost updatedBlogPost = blogPost;
        updatedBlogPost.setTitle("Updated Title");
        updatedBlogPost.setContent("Updated Content");
        updatedBlogPost.setAuthor("Updated Author");
        updatedBlogPost.setUpdatedBy("Updater");
        updatedBlogPost.setUpdatedAt(Instant.now());

        when(blogPostRepositoryMock.findById(existingBlogPost.getId())).thenReturn(Optional.of(existingBlogPost));
        // when(blogPostRepositoryMock.save(updatedBlogPost)).thenReturn(updatedBlogPost); // Can't do this as service does not call save(updatedBlogPost)
        when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(i -> i.getArgument(0)); // return the argument passed to save method

        BlogPost result = blogPostService.updateBlogPost(existingBlogPost.getId(), updatedBlogPost);

        assertAll(
            () -> assertEquals(updatedBlogPost.getTitle(), result.getTitle()),
            () -> assertEquals(updatedBlogPost.getContent(), result.getContent()),
            () -> assertEquals(updatedBlogPost.getAuthor(), result.getAuthor()),
            () -> assertEquals(updatedBlogPost.getUpdatedBy(), result.getUpdatedBy()),
            () -> assertEquals(updatedBlogPost.getUpdatedAt(), result.getUpdatedAt()),
            () -> assertNotNull(result.getUpdatedAt()),
            () -> assertEquals(existingBlogPost.getCreatedAt(), result.getCreatedAt()) // createdAt should remain unchanged
        );
        assertEquals(updatedBlogPost, result);
        verify(blogPostRepositoryMock, times(1)).findById(existingBlogPost.getId());
        verify(blogPostRepositoryMock, times(1)).save(existingBlogPost); // existingBlogPost is the one being saved after updating its fields
    }
    
    @Test
    void testUpdateBlogPost_throwsException() {
        BlogPost updatedBlogPost = blogPost;
        updatedBlogPost.setTitle("Updated Title");
        updatedBlogPost.setContent("Updated Content");
        updatedBlogPost.setAuthor("Updated Author");
        updatedBlogPost.setUpdatedBy("Updater");
        updatedBlogPost.setUpdatedAt(Instant.now());

        when(blogPostRepositoryMock.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class, 
            () -> {
                blogPostService.updateBlogPost(99L, updatedBlogPost);
            }
        );

        assertTrue(exception.getMessage().contains("99") && exception.getMessage().contains("not found"));
        verify(blogPostRepositoryMock, times(1)).findById(99L);
        verify(blogPostRepositoryMock, never()).save(any(BlogPost.class));
        verifyNoMoreInteractions(blogPostRepositoryMock);
    }
}

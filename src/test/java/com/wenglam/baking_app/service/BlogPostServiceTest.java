package com.wenglam.baking_app.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.dto.PageResponse;
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

            when(imageFileStorageServiceMock.store(blogPostCreateData.getImageFile())).thenReturn(imageUrl);

            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost createdBlogPost = blogPostService.createBlogPost(blogPostCreateData);

            ArgumentCaptor<BlogPost> blogPostCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(blogPostRepositoryMock).save(blogPostCaptor.capture());
            BlogPost blogPostSaved = blogPostCaptor.getValue();

            assertAll(
                        () -> assertNotNull(createdBlogPost),
                        () -> assertEquals(createdBlogPost.getTitle(), blogPostCreateData.getTitle()),
                        () -> assertEquals(createdBlogPost.getContent(), blogPostCreateData.getContent()),
                        () -> assertEquals(createdBlogPost.getAuthor(), blogPostCreateData.getAuthor()),
                        () -> assertEquals(createdBlogPost.getImageUrl(), imageUrl),
                        () -> assertNull(createdBlogPost.getUpdatedBy()),

                        // Assertions on Captured blogPostSaved
                        () -> assertEquals(blogPostSaved.getTitle(), blogPostCreateData.getTitle()),
                        () -> assertEquals(blogPostSaved.getContent(), blogPostCreateData.getContent()),
                        () -> assertEquals(blogPostSaved.getAuthor(), blogPostCreateData.getAuthor()),
                        () -> assertEquals(blogPostSaved.getImageUrl(), imageUrl),
                        () -> assertNull(blogPostSaved.getUpdatedBy())
                    );

            verify(imageFileStorageServiceMock, times(1)).store(blogPostCreateData.getImageFile());
            verify(blogPostRepositoryMock, times(1)).save(any(BlogPost.class));
        }

        @Test
        void createBlogPostWithoutImage_Success() {
            BlogPostCreateData blogPostCreateData = new BlogPostCreateData();
            blogPostCreateData.setTitle("Test Title");
            blogPostCreateData.setAuthor("Test Author");
            blogPostCreateData.setContent("Test Content");

            when(blogPostRepositoryMock.save(any(BlogPost.class))).thenAnswer(invocation -> invocation.getArgument(0));

            BlogPost createdBlogPost = blogPostService.createBlogPost(blogPostCreateData);

            ArgumentCaptor<BlogPost> blogPostCaptor = ArgumentCaptor.forClass(BlogPost.class);
            verify(blogPostRepositoryMock).save(blogPostCaptor.capture());
            BlogPost blogPostSaved = blogPostCaptor.getValue();

            assertAll(
                        () -> assertNotNull(createdBlogPost),
                        () -> assertEquals(createdBlogPost.getTitle(), blogPostCreateData.getTitle()),
                        () -> assertEquals(createdBlogPost.getContent(), blogPostCreateData.getContent()),
                        () -> assertEquals(createdBlogPost.getAuthor(), blogPostCreateData.getAuthor()),
                        () -> assertNull(createdBlogPost.getImageUrl()),
                        () -> assertNull(createdBlogPost.getUpdatedBy()),

                        // Assertions on Captured blogPostSaved
                        () -> assertEquals(blogPostSaved.getTitle(), blogPostCreateData.getTitle()),
                        () -> assertEquals(blogPostSaved.getContent(), blogPostCreateData.getContent()),
                        () -> assertEquals(blogPostSaved.getAuthor(), blogPostCreateData.getAuthor()),
                        () -> assertNull(blogPostSaved.getImageUrl()),
                        () -> assertNull(blogPostSaved.getUpdatedBy())
                    );

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
    class GetBlogPostsWithConditionsTests {
        @Test
        void getBlogPostsWithKeyword_Success() {
            // Given
            String keyword = "Test";
            int pageNo = 1, pageSize = 4;
            String sortBy = "id";
            String sortDir = "asc";
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);

            List<BlogPost> filteredBlogPosts = List.of(blogPost, new BlogPost(), new BlogPost(), new BlogPost());
            Page<BlogPost> pageBlogPosts = new PageImpl<>(filteredBlogPosts, pageable, 9);

            when(blogPostRepositoryMock.searchBlogPosts(keyword,pageable)).thenReturn(pageBlogPosts);

            PageResponse<BlogPost> foundBlogPosts = blogPostService.getBlogPostsWithConditions(keyword, pageable);

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(blogPostRepositoryMock).searchBlogPosts(eq(keyword), pageableCaptor.capture());

            Pageable usedPageable = pageableCaptor.getValue();

            assertEquals(0, usedPageable.getPageNumber());
            assertEquals(4, usedPageable.getPageSize());
            assertEquals(Sort.by("id").ascending(), usedPageable.getSort());

            assertNotNull(foundBlogPosts);
            assertEquals(filteredBlogPosts.size(), foundBlogPosts.content().size());
            assertEquals(filteredBlogPosts, foundBlogPosts.content());
            assertEquals(9, foundBlogPosts.totalElements());
            assertEquals(pageable.getPageNumber() + 1, foundBlogPosts.currentPage());
            assertEquals(4, foundBlogPosts.pageSize());
            assertEquals((int) Math.ceil((double) 9 / pageable.getPageSize()), foundBlogPosts.totalPages());
            assertFalse(foundBlogPosts.isLastPage());

            // It's not necessary to check whether keyword exists in contents of PageResponse object as no actual filtering operation is performed in test.

            verify(blogPostRepositoryMock, times(1)).searchBlogPosts(keyword, pageable);
            verify(blogPostRepositoryMock, never()).findAll();
        }

        @Test
        void getBlogPostsWithoutKeyword_Success() {
            // Given
            String keyword = "";
            int pageNo = 1, pageSize = 3;
            String sortBy = "id";
            String sortDir = "asc";
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);

            List<BlogPost> blogPosts = List.of();
            Page<BlogPost> pageBlogPosts = new PageImpl<>(blogPosts, pageable, 0);
            when(blogPostRepositoryMock.findAll(pageable)).thenReturn(pageBlogPosts);

            PageResponse<BlogPost> foundBlogPosts = blogPostService.getBlogPostsWithConditions(keyword, pageable);

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(blogPostRepositoryMock).findAll(pageableCaptor.capture());

            Pageable usedPageable = pageableCaptor.getValue();

            assertEquals(0, usedPageable.getPageNumber());
            assertEquals(3, usedPageable.getPageSize());
            assertEquals(Sort.by("id").ascending(), usedPageable.getSort());

            assertNotNull(foundBlogPosts);
            assertEquals(blogPosts.size(), foundBlogPosts.content().size());
            assertEquals(blogPosts, foundBlogPosts.content());
            assertEquals(0, foundBlogPosts.totalElements());
            assertEquals(pageable.getPageNumber() + 1, foundBlogPosts.currentPage());
            assertEquals(0, foundBlogPosts.pageSize());
            assertEquals((int) Math.ceil((double) 0 / pageable.getPageSize()), foundBlogPosts.totalPages());
            assertTrue(foundBlogPosts.isLastPage());

            verify(blogPostRepositoryMock, never()).searchBlogPosts(keyword, pageable);
            verify(blogPostRepositoryMock, times(1)).findAll(pageable);
        }

        @Test
        void getBlogPostsWithBlankKeyword_Success() {
            // Given
            int pageNo = 1, pageSize = 3;
            String sortBy = "id";
            String sortDir = "asc";
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);

            List<BlogPost> blogPosts = List.of(blogPost, new BlogPost(), new BlogPost());
            Page<BlogPost> pageBlogPosts = new PageImpl<>(blogPosts, pageable, 20);
            when(blogPostRepositoryMock.findAll(pageable)).thenReturn(pageBlogPosts);

            PageResponse<BlogPost> foundBlogPosts = blogPostService.getBlogPostsWithConditions(null, pageable);

            assertNotNull(foundBlogPosts);
            assertEquals(blogPosts.size(), foundBlogPosts.content().size());
            assertEquals(blogPosts, foundBlogPosts.content());
            assertEquals(20, foundBlogPosts.totalElements());
            assertEquals(pageable.getPageNumber() + 1, foundBlogPosts.currentPage());
            assertEquals(3, foundBlogPosts.pageSize());
            assertEquals((int) Math.ceil((double) 20 / pageable.getPageSize()), foundBlogPosts.totalPages());
            assertFalse(foundBlogPosts.isLastPage());

            verify(blogPostRepositoryMock, never()).searchBlogPosts(null, pageable);
            verify(blogPostRepositoryMock, times(1)).findAll(pageable);
        }

        @Test
        void getLastPageWithKeyword_Success() {
            // Given
            String keyword = "Test";
            int pageNo = 3, pageSize = 4;
            String sortBy = "id";
            String sortDir = "asc";
            Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(pageNo-1, pageSize, sort);

            List<BlogPost> filteredBlogPosts = List.of(blogPost, new BlogPost(), new BlogPost());
            Page<BlogPost> pageBlogPosts = new PageImpl<>(filteredBlogPosts, pageable, 11);
            when(blogPostRepositoryMock.searchBlogPosts(keyword,pageable)).thenReturn(pageBlogPosts);

            PageResponse<BlogPost> foundBlogPosts = blogPostService.getBlogPostsWithConditions(keyword, pageable);

            assertNotNull(foundBlogPosts);
            assertEquals(filteredBlogPosts.size(), foundBlogPosts.content().size());
            assertEquals(filteredBlogPosts, foundBlogPosts.content());
            assertEquals(11, foundBlogPosts.totalElements());
            assertEquals(pageable.getPageNumber() + 1, foundBlogPosts.currentPage());
            assertEquals(3, foundBlogPosts.pageSize());
            assertEquals((int) Math.ceil((double) 11 / pageable.getPageSize()), foundBlogPosts.totalPages());
            assertTrue(foundBlogPosts.isLastPage());

            verify(blogPostRepositoryMock, times(1)).searchBlogPosts(keyword, pageable);
            verify(blogPostRepositoryMock, never()).findAll();
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
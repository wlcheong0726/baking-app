package com.wenglam.baking_app.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.wenglam.baking_app.entity.BlogPost;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class BlogPostRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    private BlogPost blogPost = new BlogPost();

    @BeforeEach
    void setUp() {
        blogPost.setTitle("Test Title");
        blogPost.setContent("Test Content");
        blogPost.setAuthor("Test Author");
        blogPost.setImageUrl("http://localhost:8080/uploads/image.png");
    }

    @Test
    void test_whenTitleIsTooLong_shouldThrowException() {
        // Given in setUp()

        // Generate a title exceeding the maximum length of 250 characters
        String title = "Test Title............";
        for (int i = 0; i < 10; i++) {
            title += title;
        }

        blogPost.setTitle(title);

        // When & Then
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(blogPost);
        }, "Excepted PersistenceException due to title length exceeding limit");
    }

    @Test
    void test_whenAuthorIsTooLong_shouldThrowException() {
        // Given in setUp()

        // Generate an author name exceeding the maximum length of 50 characters
        String author = "Test Author............";
        for (int i = 0; i < 3; i++) {
            author += author;
        }

        blogPost.setAuthor(author);

        // When & Then
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(blogPost);
        }, "Excepted PersistenceException due to author length exceeding limit");
    }

    @Test
    void test_whenImageUrlIsTooLong_shouldThrowException() {
        // Given in setUp()

        // Generate an image URL exceeding the maximum length of 500 characters
        String imageUrl = "Test Image URL....................";
        for (int i = 0; i < 10; i++) {
            imageUrl += imageUrl;
        }

        blogPost.setAuthor(imageUrl);

        // When & Then
        assertThrows(PersistenceException.class, () -> {
            entityManager.persistAndFlush(blogPost);
        }, "Excepted PersistenceException due to imageUrl length exceeding limit");
    }

    @Test
    void test_IdAndCreateAt_isSetOnPersist() {
        // Given in setUp()

        // When
        BlogPost savedPost = entityManager.persistFlushFind(blogPost);

        // Assert
        assertTrue(savedPost.getId() > 0);
        assertNotNull(savedPost.getCreatedAt());
    }

    @Test
    void testUpdateTimestampIsUpdatedAndCreateTimeStampNeverUpdates() throws InterruptedException {
        // Given in setUp()

        // When
        BlogPost savedPost = entityManager.persistFlushFind(blogPost);
        Instant createdAt = savedPost.getCreatedAt();
        Instant updatedAt = savedPost.getUpdatedAt();

        // Wait to ensure timestamp difference
        Thread.sleep(1000);

        savedPost.setTitle("Updated Title");
        BlogPost updatedPost = entityManager.merge(savedPost); // Update entity in persistence context
        entityManager.flush(); // SQL INSERT sent to DB, but not committed yet

        // Then
        assertTrue(updatedPost.getUpdatedAt().isAfter(updatedAt));
        assertTrue(updatedPost.getUpdatedAt().isAfter(createdAt));
        assertEquals(updatedPost.getCreatedAt(), createdAt);
    }
}

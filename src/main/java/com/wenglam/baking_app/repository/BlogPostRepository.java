package com.wenglam.baking_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wenglam.baking_app.entity.BlogPost;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    @Query("""
        SELECT b FROM BlogPost b WHERE
        CAST(b.id AS string) LIKE CONCAT('%', :keyword, '%') OR
        LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
        LOWER(b.updatedBy) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<BlogPost> searchBlogPosts(@Param("keyword") String keyword, Pageable pageable);
}

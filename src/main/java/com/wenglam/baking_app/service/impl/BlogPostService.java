package com.wenglam.baking_app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;
import com.wenglam.baking_app.service.IBlogPostService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogPostService implements IBlogPostService {

    private final BlogPostRepository blogPostRepository;

    @Override
    public BlogPost createBlogPost(BlogPost blogPost) {
        return blogPostRepository.save(blogPost);
    }

    @Override
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public BlogPost getBlogPostById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BlogPost updateBlogPost(Long id, BlogPost updatedBlogPost) {
        return blogPostRepository.findById(id)
            .map(existingBlogPost -> {
                existingBlogPost.setTitle(updatedBlogPost.getTitle());
                existingBlogPost.setContent(updatedBlogPost.getContent());
                existingBlogPost.setAuthor(updatedBlogPost.getAuthor());
                existingBlogPost.setImageUrl(updatedBlogPost.getImageUrl());
                existingBlogPost.setUpdatedAt(updatedBlogPost.getUpdatedAt());
                existingBlogPost.setUpdatedBy(updatedBlogPost.getUpdatedBy());
                return blogPostRepository.save(existingBlogPost);
            })
            .orElseThrow(() -> new RuntimeException("Blog post not found with id " + id));
    }
}

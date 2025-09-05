package com.wenglam.baking_app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.persistence.EntityNotFoundException;
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
    public void deleteBlogPost(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new EntityNotFoundException("Unable to delete blog post with ID " + id + ". Blog post cannot be found.");
        }
        blogPostRepository.deleteById(id);
    }

    @Override
    public BlogPost updateBlogPost(Long id, BlogPost blogPost) {
        // TODO Auto-generated method stub
        return null;
    }
    
}

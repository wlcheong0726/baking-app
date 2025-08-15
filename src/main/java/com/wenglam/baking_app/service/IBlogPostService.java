package com.wenglam.baking_app.service;

import java.util.List;

import com.wenglam.baking_app.entity.BlogPost;

public interface IBlogPostService {

    List<BlogPost> getAllBlogPosts();
    BlogPost getBlogPostById(Long id);
    void createBlogPost(BlogPost blogPost);
    BlogPost updateBlogPost(Long id, BlogPost blogPost);
}

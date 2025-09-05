package com.wenglam.baking_app.service;

import java.util.List;

import com.wenglam.baking_app.entity.BlogPost;

public interface IBlogPostService {

    List<BlogPost> getAllBlogPosts();
    BlogPost getBlogPostById(Long id);
    BlogPost createBlogPost(BlogPost blogPost);
    void deleteBlogPost(Long id);
    BlogPost updateBlogPost(Long id, BlogPost blogPost);
}

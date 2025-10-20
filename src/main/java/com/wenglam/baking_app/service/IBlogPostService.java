package com.wenglam.baking_app.service;

import java.util.List;

import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.entity.BlogPost;

public interface IBlogPostService {

    List<BlogPost> getAllBlogPosts();
    BlogPost getBlogPostById(Long id);
    BlogPost createBlogPost(BlogPostCreateData blogPostCreateData);
    BlogPost updateBlogPost(Long id, BlogPostUpdateData updatedBlogPost);
    void deleteBlogPost(Long id);
}

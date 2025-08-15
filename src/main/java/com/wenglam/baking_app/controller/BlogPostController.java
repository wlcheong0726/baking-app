package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.service.IBlogPostService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/v1/blogposts")
@RequiredArgsConstructor // lombok - generate constructors
// @CrossOrigin(origins = "http://localhost:5173")
public class BlogPostController {
    
    private final IBlogPostService blogPostService;

    @GetMapping
    public List<BlogPost> getAllBlogPosts() {
        return blogPostService.getAllBlogPosts();
    }

    @PostMapping
    public void createBlogPost(@RequestBody BlogPost blogPost) {
        blogPostService.createBlogPost(blogPost);
    }
}

package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.service.IBlogPostService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;




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

    @GetMapping("/blogpost/{id}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Long id) {
        BlogPost blogPost = blogPostService.getBlogPostById(id);

        if (blogPost == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(blogPost);
        }
    }

    @PostMapping
    public BlogPost createBlogPost(@RequestBody BlogPost blogPost) {
        return blogPostService.createBlogPost(blogPost);
    }

    @PutMapping("/blogpost/{id}")
    public BlogPost editBlogPost(@PathVariable Long id, @RequestBody BlogPost updatedBlogPost) {
        return blogPostService.updateBlogPost(id, updatedBlogPost);
    }
}

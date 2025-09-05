package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping
    public BlogPost createBlogPost(@RequestBody BlogPost blogPost) {
        return blogPostService.createBlogPost(blogPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBlogPost(@PathVariable Long id) {
        try {
            blogPostService.deleteBlogPost(id);
            return new ResponseEntity<>("Blog post with ID " + id + " deleted successfully.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}

package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/blogposts")
@RequiredArgsConstructor // lombok - generate constructors
// @CrossOrigin(origins = "http://localhost:5173")
public class BlogPostController {
    private final IBlogPostService blogPostService;

    @GetMapping
    public ResponseEntity<?> getAllBlogPosts() {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts());
    }

    @GetMapping("/blogpost/{id}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getBlogPostById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBlogPost(@ModelAttribute BlogPostCreateData blogPostCreateData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostService.createBlogPost(blogPostCreateData));
    }

    @PutMapping(value = "/blogpost/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editBlogPost(@PathVariable Long id, 
                                            @ModelAttribute BlogPostUpdateData blogPostUpdateData) {
        return ResponseEntity.ok(blogPostService.updateBlogPost(id, blogPostUpdateData));
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

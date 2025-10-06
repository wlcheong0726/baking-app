package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;
import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
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
    public ResponseEntity<?> createBlogPost(@Valid @ModelAttribute BlogPostCreateData blogPostCreateData) {
        log.info("Create blog post title='{}' author='{}'", blogPostCreateData.getTitle(), blogPostCreateData.getAuthor());
        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostService.createBlogPost(blogPostCreateData));
    }

    @PutMapping(value = "/blogpost/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> editBlogPost(@PathVariable Long id, 
                                            @Valid @ModelAttribute BlogPostUpdateData blogPostUpdateData) {
        log.info("Update blog post id={} title='{}' author='{}'", id, blogPostUpdateData.getTitle(), blogPostUpdateData.getAuthor());
        return ResponseEntity.ok(blogPostService.updateBlogPost(id, blogPostUpdateData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
            blogPostService.deleteBlogPost(id);
            log.info("Delete blog post id={}", id);
            return ResponseEntity.noContent().build(); // 204 No Content
    }
}

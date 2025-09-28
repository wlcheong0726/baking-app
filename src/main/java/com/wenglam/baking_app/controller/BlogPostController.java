package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.service.FileStorageService;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final FileStorageService fileStorageService;

    @GetMapping
    public ResponseEntity<?> getAllBlogPosts() {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts());
    }

    @GetMapping("/blogpost/{id}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getBlogPostById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBlogPost(@RequestParam String title, 
                                            @RequestParam String content,
                                            @RequestParam String author,
                                            @RequestParam(required = false) MultipartFile imageFile) {
        
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            // Save the file to disk and get the URL
            imageUrl = fileStorageService.store(imageFile); // Placeholder URL
            // In a real application, you would save the file and generate a proper URL
        }

        // Construct the BlogPost object from params
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(title);
        blogPost.setContent(content);
        blogPost.setAuthor(author);
        blogPost.setImageUrl(imageUrl);

        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostService.createBlogPost(blogPost));
    }

    @PutMapping("/blogpost/{id}")
    public ResponseEntity<?> editBlogPost(@PathVariable Long id, @RequestBody BlogPost updatedBlogPost) {
        return ResponseEntity.ok(blogPostService.updateBlogPost(id, updatedBlogPost));
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

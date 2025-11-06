package com.wenglam.baking_app.controller;

import org.springframework.web.bind.annotation.RestController;
import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.dto.PageResponse;
import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.service.IBlogPostService;
import com.wenglam.baking_app.utility.UrlBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllBlogPosts() {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts());
    }

    @GetMapping
    public ResponseEntity<PageResponse<BlogPost>> getBlogPostsWithConditions(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                        @RequestParam(required = false, defaultValue = "20") int pageSize,
                                                        @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                        @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                        @RequestParam(required = false) String keyword
                                                        ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        return ResponseEntity.ok(blogPostService.getBlogPostsWithConditions(keyword, PageRequest.of(pageNo-1, pageSize, sort)));
    }

    @GetMapping("/blogpost/{id}")
    public ResponseEntity<?> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getBlogPostById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createBlogPost(@Valid @ModelAttribute BlogPostCreateData blogPostCreateData) {
        log.info("Create blog post title='{}' author='{}'", blogPostCreateData.getTitle(), blogPostCreateData.getAuthor());

        BlogPost createdBlogPost = blogPostService.createBlogPost(blogPostCreateData);

        // Construct absolute image url if image Url is present
        if (createdBlogPost != null && createdBlogPost.getImageUrl() != null) {
            createdBlogPost.setImageUrl(UrlBuilder.buildFullUrl(createdBlogPost.getImageUrl()));
            log.info("Constructed absolute image URL: {}", createdBlogPost.getImageUrl());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBlogPost);
    }

    @PutMapping(value = "/blogpost/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateBlogPost(@PathVariable Long id, 
                                            @Valid @ModelAttribute BlogPostUpdateData blogPostUpdateData) {
        log.info("Update blog post id={} title='{}' author='{}'", id, blogPostUpdateData.getTitle(), blogPostUpdateData.getAuthor());

        BlogPost updatedBlogPost = blogPostService.updateBlogPost(id, blogPostUpdateData);

        // Construct absolute image url if image Url is present
        if (updatedBlogPost != null && updatedBlogPost.getImageUrl() != null) {
            updatedBlogPost.setImageUrl(UrlBuilder.buildFullUrl(updatedBlogPost.getImageUrl()));
            log.info("Constructed absolute image URL: {}", updatedBlogPost.getImageUrl());
        }
        return ResponseEntity.ok(updatedBlogPost);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) {
            blogPostService.deleteBlogPost(id);
            log.info("Delete blog post id={}", id);
            return ResponseEntity.noContent().build(); // 204 No Content
    }
}

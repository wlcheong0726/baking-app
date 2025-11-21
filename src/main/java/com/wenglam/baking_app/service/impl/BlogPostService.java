package com.wenglam.baking_app.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.dto.PageResponse;
import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;
import com.wenglam.baking_app.service.ImageFileStorageService;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogPostService implements IBlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final ImageFileStorageService imageFileStorageService;

    @Override
    public BlogPost createBlogPost(BlogPostCreateData blogPostCreateData) {

        log.debug("Blog post create data from frontend: " + blogPostCreateData.toString());

        String imageUrl = null;
        if (blogPostCreateData.getImageFile() != null && !blogPostCreateData.getImageFile().isEmpty()) {
            imageUrl = imageFileStorageService.store(blogPostCreateData.getImageFile());
        }

        BlogPost blogPostToBeCreated = new BlogPost();
        blogPostToBeCreated.setTitle(blogPostCreateData.getTitle());
        blogPostToBeCreated.setContent(blogPostCreateData.getContent());
        blogPostToBeCreated.setAuthor(blogPostCreateData.getAuthor());
        blogPostToBeCreated.setImageUrl(imageUrl);

        log.info("Creating blog post title='{}' author='{}'", blogPostToBeCreated.getTitle(), blogPostToBeCreated.getAuthor());

        return blogPostRepository.save(blogPostToBeCreated);
    }

    @Override
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public PageResponse<BlogPost> getBlogPostsWithConditions(String keyword, Pageable pageable) {
        Page<BlogPost> pageBlogPosts;
        
        if (keyword != null && !keyword.isEmpty()) {
            log.info("Searching blog posts with keyword='{}'", keyword);
            pageBlogPosts = blogPostRepository.searchBlogPosts(keyword, pageable);
        } else {
            pageBlogPosts = blogPostRepository.findAll(pageable);
        }

        return new PageResponse<BlogPost>(
                pageBlogPosts.getTotalElements(), // total num of elements
                pageBlogPosts.getContent(), // list of blog posts
                pageBlogPosts.getNumber() + 1, // current page
                pageBlogPosts.getNumberOfElements(), // page size
                pageBlogPosts.getTotalPages(),
                pageBlogPosts.isLast() // is last page
            );
    }

    @Override
    public BlogPost getBlogPostById(Long id) {
        if (!blogPostRepository.existsById(id)) {
            log.warn("Blog post not found id={}", id);

            throw new EntityNotFoundException(
                    "Unable to find blog post with id: " + id + ".");
        }

        return blogPostRepository.findById(id).get();
    }

    @Override
    public BlogPost updateBlogPost(Long id, BlogPostUpdateData blogPostUpdateData) {

        log.debug("Blog post update data from frontend: " + blogPostUpdateData.toString());
        BlogPost blogPostToBeUpdated = blogPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Blog post not found with id " + id));

        String imageUrl = null; // For scenarios where pic gets deleted or never was uploaded
        if (blogPostUpdateData.getImageUrl() != null && blogPostToBeUpdated.getImageUrl() != null) {
            // Handles scenario where the uploaded picture remains the same
            imageUrl = blogPostToBeUpdated.getImageUrl(); 
        } else if (blogPostUpdateData.getImageFile() != null && !blogPostUpdateData.getImageFile().isEmpty()) {
            // Handles scenario where the uploaded picture has been changed
            imageUrl = imageFileStorageService.store(blogPostUpdateData.getImageFile());
        }

        blogPostToBeUpdated.setTitle(blogPostUpdateData.getTitle());
        blogPostToBeUpdated.setContent(blogPostUpdateData.getContent());
        blogPostToBeUpdated.setAuthor(blogPostUpdateData.getAuthor());
        blogPostToBeUpdated.setImageUrl(imageUrl);
        blogPostToBeUpdated.setUpdatedAt(Instant.now()); // TODO: add updated at datetime
        blogPostToBeUpdated.setUpdatedBy(blogPostUpdateData.getAuthor()); // Amend in the future if allow other users to update

        log.info("Updating blog post id={} title='{}' author='{}'", blogPostToBeUpdated.getId(), blogPostToBeUpdated.getTitle(), blogPostToBeUpdated.getAuthor());

        return blogPostRepository.save(blogPostToBeUpdated);
    }

    public void deleteBlogPost(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Unable to delete blog post with ID " + id + ". Blog post cannot be found.");
        }

        log.info("Deleting blog post id={}", id);

        blogPostRepository.deleteById(id);
    }
}

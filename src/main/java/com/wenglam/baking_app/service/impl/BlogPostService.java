package com.wenglam.baking_app.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wenglam.baking_app.dto.BlogPostCreateData;
import com.wenglam.baking_app.dto.BlogPostUpdateData;
import com.wenglam.baking_app.entity.BlogPost;
import com.wenglam.baking_app.repository.BlogPostRepository;
import com.wenglam.baking_app.service.FileStorageService;
import com.wenglam.baking_app.service.IBlogPostService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BlogPostService implements IBlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final FileStorageService fileStorageService;

    @Override
    public BlogPost createBlogPost(BlogPostCreateData blogPostCreateData) {

        String imageUrl = null;
        if (blogPostCreateData.getImageFile() != null && !blogPostCreateData.getImageFile().isEmpty()) {
            imageUrl = fileStorageService.store(blogPostCreateData.getImageFile());
        }

        BlogPost blogPostToBeCreated = new BlogPost();
        blogPostToBeCreated.setAuthor(blogPostCreateData.getAuthor());
        blogPostToBeCreated.setContent(blogPostCreateData.getContent());
        blogPostToBeCreated.setAuthor(blogPostCreateData.getAuthor());
        blogPostToBeCreated.setImageUrl(imageUrl);

        return blogPostRepository.save(blogPostToBeCreated);
    }

    @Override
    public List<BlogPost> getAllBlogPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public BlogPost getBlogPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Blog post with ID " + id + " not found."));
    }

    @Override
    public BlogPost updateBlogPost(Long id, BlogPostUpdateData blogPostUpdateData) {

        System.out.println("blog post update data from frontend: " + blogPostUpdateData.toString());
        BlogPost blogPostToBeUpdated = blogPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Blog post not found with id " + id));

        String imageUrl = null; // For scenarios where pic gets deleted or never was uploaded
        if (blogPostUpdateData.getImageUrl() != null && blogPostUpdateData.getImageUrl().equals(blogPostToBeUpdated.getImageUrl())) {
            // Handles scenario where the uploaded picture remains the same
            imageUrl = blogPostUpdateData.getImageUrl(); 
        } else if (blogPostUpdateData.getImageFile() != null && !blogPostUpdateData.getImageFile().isEmpty()) {
            // Handles scenario where the uploaded picture has been changed
            imageUrl = fileStorageService.store(blogPostUpdateData.getImageFile());
        }

        blogPostToBeUpdated.setTitle(blogPostUpdateData.getTitle());
        blogPostToBeUpdated.setContent(blogPostUpdateData.getContent());
        blogPostToBeUpdated.setAuthor(blogPostUpdateData.getAuthor());
        blogPostToBeUpdated.setImageUrl(imageUrl);
        blogPostToBeUpdated.setUpdatedAt(Instant.now()); // TODO: add updated at datetime
        blogPostToBeUpdated.setUpdatedBy(blogPostUpdateData.getAuthor()); // Amend in the future if allow other users to update

        return blogPostRepository.save(blogPostToBeUpdated);
    }

    public void deleteBlogPost(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new EntityNotFoundException(
                    "Unable to delete blog post with ID " + id + ". Blog post cannot be found.");
        }
        blogPostRepository.deleteById(id);
    }
}

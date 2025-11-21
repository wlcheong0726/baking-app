package com.wenglam.baking_app.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BlogPostBaseData {

    @NotBlank(message = "Title cannot be empty or contain only whitespaces.")
    @Size(max = 250, message = "Title must not exceed 250 characters.")
    protected String title;

    @NotBlank(message = "Author cannot be empty or contain only whitespaces.")
    @Size(max = 50, message = "Author name must not exceed 50 characters.")
    protected String author;

    @NotBlank(message = "Content cannot be empty or contain only whitespaces.")
    protected String content;

    protected MultipartFile imageFile;
}
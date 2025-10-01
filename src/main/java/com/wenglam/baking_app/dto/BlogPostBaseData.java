package com.wenglam.baking_app.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BlogPostBaseData {
    protected String title;
    protected String content;
    protected String author;
    protected MultipartFile imageFile;
}

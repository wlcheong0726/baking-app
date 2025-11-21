package com.wenglam.baking_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

import com.wenglam.baking_app.controller.BlogPostController;

@SpringBootTest
public class SmokeTest {
    
    @Autowired
    private BlogPostController blogPostController;

    @Test
    void contextLoads() {
        assertThat(blogPostController).isNotNull();
    }
}
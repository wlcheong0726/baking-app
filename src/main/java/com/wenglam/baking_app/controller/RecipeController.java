package com.wenglam.baking_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wenglam.baking_app.service.impl.RecipeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/recipes")
@RequiredArgsConstructor
public class RecipeController {
    
    private final RecipeService recipeService;

    @GetMapping("/")
    public ResponseEntity<?> getDessertRecipes() {
        log.info("Received request to fetch dessert recipes");
        return ResponseEntity.ok(recipeService.getDessertRecipes());
    }
}

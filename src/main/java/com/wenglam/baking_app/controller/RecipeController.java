package com.wenglam.baking_app.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    public ResponseEntity<?> getDessertRecipes(@RequestParam(required = false, defaultValue = "1") int pageNo,
                                                        @RequestParam(required = false, defaultValue = "20") int pageSize,
                                                        @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                        @RequestParam(required = false) String keyword
                                                        ) {
        log.info("Received request to fetch dessert recipes");

        // add sort functionality later

        return ResponseEntity.ok(recipeService.getDessertRecipes(keyword, PageRequest.of(pageNo-1, pageSize)));
    }
}

package com.wenglam.baking_app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wenglam.baking_app.dto.RecipeResponseDto;
import com.wenglam.baking_app.external.tasty.TastyClient;
import com.wenglam.baking_app.external.tasty.TastyListResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeService {
    private final TastyClient tastyClient;
    private final String apiKey = "";

    public List<RecipeResponseDto> getDessertRecipes() {
        log.info("Fetching dessert recipes from Tasty API");

        TastyListResponseDto dessertRecipes = tastyClient.getDessertRecipes(
            "tasty.p.rapidapi.com",
            apiKey,
            0,
            10,
            "",
            "dessert",
            ""
        );

        return dessertRecipes.results().stream()
            .map(recipe -> new RecipeResponseDto(
                recipe.id(),
                recipe.name(),
                recipe.description(),
                recipe.num_servings(),
                recipe.thumbnail_url(),
                // recipe.tags(),
                // recipe.nutrition(),
                recipe.instructions().stream().map(instr -> new RecipeResponseDto.Instruction(
                    instr.appliance(),
                    instr.display_text(),
                    instr.end_time(),
                    instr.id(),
                    instr.position(),
                    instr.start_time(),
                    instr.temperature())).toList()
            ))
            .toList();
    }

}

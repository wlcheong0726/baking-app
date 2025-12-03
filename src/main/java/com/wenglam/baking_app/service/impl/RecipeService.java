package com.wenglam.baking_app.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wenglam.baking_app.config.TastyApiProperties;
import com.wenglam.baking_app.dto.PageResponseDto;
import com.wenglam.baking_app.dto.RecipeResponseDto;
import com.wenglam.baking_app.external.tasty.TastyClient;
import com.wenglam.baking_app.external.tasty.TastyListResponseDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeService {
    private final TastyClient tastyClient;
    private final TastyApiProperties tastyApiProperties;

    public PageResponseDto<RecipeResponseDto> getDessertRecipes(String keyword, Pageable pageable) {
        log.info("Fetching dessert recipes from Tasty API");

        String rapidApiHost = tastyApiProperties.getRapidapiHost();
        String apiKey = tastyApiProperties.getRapidapiKey();

         // Convert frontend inputs to Tasty API parameters
        int fromIndex = (int) pageable.getOffset(); // page number * page size
        String query = (keyword != null && !keyword.isEmpty()) ? "dessert " + keyword : "dessert";

        TastyListResponseDto tastyDessertRecipesResponse = tastyClient.getDessertRecipes(
            rapidApiHost,
            apiKey,
            fromIndex,
            pageable.getPageSize(),
            "",
            query,
            "" // popular by default, other options: approved_at:desc|approved_at:asc - not very useful, to be changed later depending on fronend requirements
        );

        List<RecipeResponseDto> dessertRecipeResponseDtos = tastyDessertRecipesResponse.results().stream()
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
        
        int totalElements = tastyDessertRecipesResponse.count();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        boolean isLast = ((pageable.getPageNumber() + 1) >= totalPages);
        
        return new PageResponseDto<>(
            totalElements,
            dessertRecipeResponseDtos,
            pageable.getPageNumber() + 1,
            pageable.getPageSize(),
            totalPages,
            isLast
        );
    }
}

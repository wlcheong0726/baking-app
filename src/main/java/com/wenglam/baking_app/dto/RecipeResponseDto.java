package com.wenglam.baking_app.dto;

import java.util.List;

public record RecipeResponseDto(
    int id,
    String name,
    String description,
    int num_servings,
    String thumbnail_url,
    // List<String> tags,
    // List<Nutrition> nutrition,
    List<Instruction> instructions
) {
    public record Instruction(
        String appliance,
        String display_text,
        int end_time,
        int id,
        int position,
        int start_time,
        int temperature
    ) {

    }
}

package com.wenglam.baking_app.dto;

import java.util.List;

/**
 * A generic record that wraps paginated data returned from the API.
 * <p>
 * Simplifies the JSON response structure by exposing only the relevant pagination details
 * needed by the frontend, while hiding Spring Boot's internal Page implementation.
 * 
 * @param <T> the type of elements contained in the paginated response.
 */
public record PageResponseDto<T>(
    long totalElements,
    List<T> content,
    int currentPage,
    int pageSize,
    int totalPages,
    boolean isLastPage
) {
}
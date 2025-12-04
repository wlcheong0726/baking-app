package com.wenglam.baking_app.exception;

public class TastyRecipeNotFoundException extends RuntimeException {
    public TastyRecipeNotFoundException(String message) {
        super(message);
    }
}

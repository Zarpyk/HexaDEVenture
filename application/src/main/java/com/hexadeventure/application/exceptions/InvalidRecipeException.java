package com.hexadeventure.application.exceptions;

public class InvalidRecipeException extends RuntimeException {
    public InvalidRecipeException(int index) {
        super("Invalid recipe with index " + index);
    }
}

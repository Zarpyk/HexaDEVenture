package com.hexadeventure.application.exceptions;

public class InvalidRecipeException extends RuntimeException {
    public InvalidRecipeException(int index) {
        super("The given recipe with index " + index + " is invalid");
    }
}

package com.hexadeventure.application.exceptions;

public class InvalidPositionException extends RuntimeException {
    public InvalidPositionException(String message) {
        super("The given position is invalid: " + message);
    }
}

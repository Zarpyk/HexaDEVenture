package com.hexadeventure.application.exceptions;

public class InvalidPositionException extends RuntimeException {
    public InvalidPositionException() {
        super("Invalid position");
    }
}

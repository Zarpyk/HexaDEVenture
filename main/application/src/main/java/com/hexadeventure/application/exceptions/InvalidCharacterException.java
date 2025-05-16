package com.hexadeventure.application.exceptions;

public class InvalidCharacterException extends RuntimeException {
    public InvalidCharacterException() {
        super("The given character is invalid");
    }
}

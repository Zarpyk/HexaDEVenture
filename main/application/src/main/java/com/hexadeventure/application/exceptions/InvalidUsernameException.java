package com.hexadeventure.application.exceptions;

public class InvalidUsernameException extends RuntimeException {
    public InvalidUsernameException() {
        super("Username cannot be empty or null");
    }
}

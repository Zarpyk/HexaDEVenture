package com.hexadeventure.application.exceptions;

public class UserExistException extends RuntimeException {
    public UserExistException(String email) {
        super("User with email " + email + " already exists");
    }
}

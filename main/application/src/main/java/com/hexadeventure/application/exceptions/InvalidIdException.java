package com.hexadeventure.application.exceptions;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException() {
        super("Given Id is invalid");
    }
}

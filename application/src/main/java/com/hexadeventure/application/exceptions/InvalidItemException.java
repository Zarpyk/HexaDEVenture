package com.hexadeventure.application.exceptions;

public class InvalidItemException extends RuntimeException {
    public InvalidItemException() {
        super("The given item is invalid");
    }
}

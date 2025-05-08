package com.hexadeventure.application.exceptions;

public class InvalidSearchException extends RuntimeException {
    public InvalidSearchException() {
        super("Invalid page or size");
    }
}

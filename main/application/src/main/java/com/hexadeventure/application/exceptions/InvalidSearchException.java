package com.hexadeventure.application.exceptions;

public class InvalidSearchException extends RuntimeException {
    public InvalidSearchException() {
        super("Given page or size is invalid");
    }
}

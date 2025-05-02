package com.hexadeventure.application.exceptions;

public class PositionEmptyException extends RuntimeException {
    public PositionEmptyException() {
        super("This position does not contain any character");
    }
}

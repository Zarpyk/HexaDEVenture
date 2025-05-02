package com.hexadeventure.application.exceptions;

public class PositionOccupiedException extends RuntimeException {
    public PositionOccupiedException() {
        super("This position is already occupied by another character");
    }
}

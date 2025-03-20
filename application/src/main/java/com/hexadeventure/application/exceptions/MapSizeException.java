package com.hexadeventure.application.exceptions;

public class MapSizeException extends RuntimeException {
    public MapSizeException(int minSize) {
        super("Map size must be greater than " + minSize);
    }
}

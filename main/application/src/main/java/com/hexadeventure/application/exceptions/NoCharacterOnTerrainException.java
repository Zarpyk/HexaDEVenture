package com.hexadeventure.application.exceptions;

public class NoCharacterOnTerrainException extends RuntimeException {
    public NoCharacterOnTerrainException() {
        super("No character on the terrain");
    }
}

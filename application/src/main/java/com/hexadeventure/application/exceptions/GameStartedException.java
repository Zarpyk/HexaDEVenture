package com.hexadeventure.application.exceptions;

public class GameStartedException extends RuntimeException {
    public GameStartedException() {
        super("Game already started");
    }
}

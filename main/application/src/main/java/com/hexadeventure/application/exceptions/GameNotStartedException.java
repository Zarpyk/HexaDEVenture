package com.hexadeventure.application.exceptions;

public class GameNotStartedException extends RuntimeException {
    public GameNotStartedException() {
        super("Game not started");
    }
}

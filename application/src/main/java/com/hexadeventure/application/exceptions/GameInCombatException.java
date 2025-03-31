package com.hexadeventure.application.exceptions;

public class GameInCombatException extends RuntimeException {
    public GameInCombatException() {
        super("Game is in combat");
    }
}

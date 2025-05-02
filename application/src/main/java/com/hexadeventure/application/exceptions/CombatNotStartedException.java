package com.hexadeventure.application.exceptions;

public class CombatNotStartedException extends RuntimeException {
    public CombatNotStartedException() {
        super("Combat not started");
    }
}

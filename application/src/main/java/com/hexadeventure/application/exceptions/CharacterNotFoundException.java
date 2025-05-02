package com.hexadeventure.application.exceptions;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException() {
        super("Character not found on the inventory.");
    }
}

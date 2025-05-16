package com.hexadeventure.application.exceptions;

public class NotEnoughResourcesException extends RuntimeException {
    public NotEnoughResourcesException() {
        super("Not enough resources");
    }
}

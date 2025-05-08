package com.hexadeventure.application.exceptions;

public class NotEnoughtResourcesException extends RuntimeException {
    public NotEnoughtResourcesException() {
        super("Not enough resources");
    }
}

package com.hexadeventure.application.exceptions;

public class InvalidPasswordException extends RuntimeException {
    
    public InvalidPasswordException() {
        super("""
              Password cannot be null and must:
               - be between 8 and 64 characters long
               - contain at least one uppercase letter
               - contain at least one lowercase letter
               - contain at least one digit
               - contain at least one special character (!@#$&*_-) or space""");
    }
}

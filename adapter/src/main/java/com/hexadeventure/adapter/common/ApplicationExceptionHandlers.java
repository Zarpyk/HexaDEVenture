package com.hexadeventure.adapter.common;

import com.hexadeventure.application.exceptions.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationExceptionHandlers {
    @ResponseBody
    @ExceptionHandler({GameStartedException.class,
                       GameNotStartedException.class,
                       CombatNotStartedException.class,
                       PositionEmptyException.class,
                       PositionOccupiedException.class,
                       CharacterNotFoundException.class,
                       NotEnoughtResourcesException.class})
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    private String methodNotAllowedExceptionHandler(Exception ex) {
        return ex.getMessage();
    }
    
    @ResponseBody
    @ExceptionHandler(UserExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    private String conflictExceptionHandler(Exception ex) {
        return ex.getMessage();
    }
    
    @ResponseBody
    @ExceptionHandler({SizeException.class,
                       InvalidPositionException.class,
                       InvalidSearchException.class,
                       InvalidRecipeException.class,
                       InvalidCharacterException.class,
                       InvalidItemException.class,
                       InvalidEmailException.class,
                       InvalidUsernameException.class,
                       InvalidPasswordException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private String badRequestExceptionHandler(Exception ex) {
        return ex.getMessage();
    }
}
package com.hexadeventure.adapter.common;

import com.hexadeventure.application.exceptions.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Hidden
public class ApplicationExceptionHandlers {
    @ResponseBody
    @ExceptionHandler({GameStartedException.class,
                       GameNotStartedException.class,
                       GameInCombatException.class,
                       CombatNotStartedException.class,
                       NotEnoughResourcesException.class})
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
                       InvalidPasswordException.class,
                       InvalidIdException.class,
                       NoCharacterOnTerrainException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private String badRequestExceptionHandler(Exception ex) {
        return ex.getMessage();
    }
}
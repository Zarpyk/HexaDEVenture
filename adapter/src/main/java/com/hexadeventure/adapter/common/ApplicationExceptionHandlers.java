package com.hexadeventure.adapter.common;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.hexadeventure.application.exceptions.GameStartedException;
import com.hexadeventure.application.exceptions.UserExistException;


@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApplicationExceptionHandlers {
    @ResponseBody
    @ExceptionHandler(GameStartedException.class)
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
}
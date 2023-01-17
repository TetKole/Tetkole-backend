package com.tetkole.restservice.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ErrorResponse resourceNotFoundException(MethodArgumentNotValidException e) {
        return ErrorResponse.create(e, HttpStatus.BAD_REQUEST, e.getMessage());
    }
}

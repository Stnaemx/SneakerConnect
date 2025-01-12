package com.Sneaker.SneakerConnect.config;

import com.Sneaker.SneakerConnect.exceptions.DtoNotValidException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DtoNotValidException.class)
    public ResponseEntity<?> handleException(DtoNotValidException exp) {
        return ResponseEntity.badRequest().body(exp.getErrorMessages());
    }
}

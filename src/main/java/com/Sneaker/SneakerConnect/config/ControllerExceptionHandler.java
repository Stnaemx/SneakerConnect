package com.Sneaker.SneakerConnect.config;

import com.Sneaker.SneakerConnect.exceptions.DtoNotValidException;
import com.Sneaker.SneakerConnect.exceptions.UserAlreadyExistsException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(DtoNotValidException.class)
    public ResponseEntity<?> handleException(DtoNotValidException exp) {
        return ResponseEntity.badRequest().body(exp.getErrorMessages());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleException(UserAlreadyExistsException exp) {
        return ResponseEntity.badRequest().body(exp.getMessage());
    }
}

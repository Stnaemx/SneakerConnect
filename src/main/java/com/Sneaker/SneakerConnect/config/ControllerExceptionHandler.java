//package com.Sneaker.SneakerConnect.config;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class ControllerExceptionHandler {
//
//    @ResponseStatus(HttpStatus.CONFLICT)  // 409
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public void handleConflict() {
//    }
//
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        // Map to store field-specific validation error messages
//        Map<String, String> errors = new HashMap<>();
//
//        // Extract validation errors from the exception
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage())
//        );
//
//        // Return the errors wrapped in a ResponseEntity
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
//}

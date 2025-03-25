package com.example.command.config.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomHttpExceptionResponse> handleRuntimeException(RuntimeException e) {
        CustomHttpDetail badRequest = CustomHttpDetail.BAD_REQUEST;
        return ResponseEntity.status(badRequest.getStatusCode()).body(new CustomHttpExceptionResponse(badRequest.getStatusCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomHttpExceptionResponse> handleException(Exception e) {
        CustomHttpDetail internalServerError = CustomHttpDetail.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(internalServerError.getStatusCode()).body(new CustomHttpExceptionResponse(internalServerError.getStatusCode(), e.getMessage()));
    }
}

package com.bohdan.urlshortener.exception;

import com.bohdan.urlshortener.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AliasAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAliasAlreadyExists(AliasAlreadyExistsException e, HttpServletRequest request) {
        return getResponseEntity(HttpStatus.CONFLICT, e.getMessage(), "Alias already exists", request);
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<ErrorResponse> handleUrlExpired(UrlExpiredException e, HttpServletRequest request) {
        return getResponseEntity(HttpStatus.GONE, e.getMessage(), "Url expired", request);
    }

    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUrlNotFound(UrlNotFoundException e, HttpServletRequest request) {
        return getResponseEntity(HttpStatus.NOT_FOUND, e.getMessage(), "Url not found", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        return getResponseEntity(HttpStatus.BAD_REQUEST, e.getMessage(), "Method argument not valid", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request) {
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "Internal Server Error", request);
    }

    private static @NonNull ResponseEntity<ErrorResponse> getResponseEntity(HttpStatus status,
                                                                            String message,
                                                                            String errorMessage,
                                                                            HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                errorMessage,
                message,
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, status);
    }

}

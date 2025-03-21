package com.elams.globalexceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.demo.exceptions.ResourceNotFoundException;

import jakarta.servlet.ServletException;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * This class handles various exceptions and provides appropriate responses with HTTP status codes.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	 Map<String, String> response = new HashMap<>();
	 String er="error";
    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex The ResourceNotFoundException instance.
     * @return ResponseEntity containing error message and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
       
        response.put(er, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles MethodArgumentNotValidException (validation errors).
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return ResponseEntity containing validation error messages and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles ServletException.
     *
     * @param ex The IllegalArgumentException instance.
     * @return ResponseEntity containing error message and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(ServletException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException1(IllegalArgumentException ex) {
        response.put(er, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex The IllegalArgumentException instance.
     * @return ResponseEntity containing error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
     
        response.put(er, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles generic Exception.
     *
     * @param ex The Exception instance.
     * @return ResponseEntity containing a generic error message, details, and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
       
        response.put(er, "An unexpected error occurred. Please try again.");
        response.put("details", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
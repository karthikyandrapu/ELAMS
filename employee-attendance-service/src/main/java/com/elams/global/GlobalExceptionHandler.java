package com.elams.global;

import com.elams.exception.ClockInNotFoundException;
import com.elams.exception.InvalidEmployeeIdException;
import com.elams.aop.AppLogger;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 * This class handles exceptions thrown across different controllers and provides
 * consistent error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = AppLogger.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles InvalidEmployeeIdException and returns a BAD_REQUEST response.
     *
     * @param ex The InvalidEmployeeIdException to handle.
     * @return A ResponseEntity with the exception message and BAD_REQUEST status.
     */
    @ExceptionHandler(InvalidEmployeeIdException.class)
    public ResponseEntity<String> handleInvalidEmployeeIdException(InvalidEmployeeIdException ex) {
        logger.error("InvalidEmployeeIdException caught: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles IllegalArgumentException and returns a BAD_REQUEST response with an ErrorResponse body.
     *
     * @param ex The IllegalArgumentException to handle.
     * @return A ResponseEntity with an ErrorResponse and BAD_REQUEST status.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException caught: {}", ex.getMessage());
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * Inner class to represent the error response body.
     */
    static class ErrorResponse {
        private int statusCode;
        private String message;

        /**
         * Constructs a new ErrorResponse with the specified status code and message.
         *
         * @param statusCode The HTTP status code.
         * @param message    The error message.
         */
        public ErrorResponse(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        /**
         * Gets the HTTP status code.
         *
         * @return The status code.
         */
        public int getStatusCode() {
            return statusCode;
        }

        /**
         * Gets the error message.
         *
         * @return The error message.
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Handles ClockInNotFoundException and returns a NOT_FOUND response.
     *
     * @param ex The ClockInNotFoundException to handle.
     * @return A ResponseEntity with a specific error message and NOT_FOUND status.
     */
    @ExceptionHandler(ClockInNotFoundException.class)
    public ResponseEntity<String> handleClockInNotFoundException(ClockInNotFoundException ex) {
        logger.error("ClockInNotFoundException caught: {}", ex.getMessage());
        return new ResponseEntity<>("No active clock-in found for this employee.", HttpStatus.NOT_FOUND);
    }
}
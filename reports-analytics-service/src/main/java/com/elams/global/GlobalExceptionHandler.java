package com.elams.global;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.elams.exception.AttendanceReportCreationException;
import com.elams.exception.AttendanceReportDeletionException;
import com.elams.exception.AttendanceReportValidationException;
import com.elams.exception.ResourceNotFoundException;


/**
 * Global exception handler for the application.
 * This class handles various exceptions and returns appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException and returns a 404 NOT FOUND response.
     *
     * @param ex The ResourceNotFoundException instance.
     * @return ResponseEntity with a 404 status and the exception message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    /**
     * Handles AttendanceReportDeletionException and returns a 400 BAD REQUEST response.
     *
     * @param ex The AttendanceReportDeletionException instance.
     * @return ResponseEntity with a 400 status and the exception message.
     */
    @ExceptionHandler(AttendanceReportDeletionException.class)
    public ResponseEntity<String> handleAttendanceReportDeletionException(AttendanceReportDeletionException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    /**
     * Handles AttendanceReportValidationException and returns a 400 BAD REQUEST response.
     *
     * @param ex The AttendanceReportValidationException instance.
     * @return ResponseEntity with a 400 status and the exception message.
     */
    @ExceptionHandler(AttendanceReportValidationException.class)
    public ResponseEntity<String> handleAttendanceReportValidationException(AttendanceReportValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException and returns a 400 BAD REQUEST response.
     *
     * @param ex The IllegalArgumentException instance.
     * @return ResponseEntity with a 400 status and the exception message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    /**
     * Handles AttendanceReportCreationException and returns a 400 BAD REQUEST response.
     *
     * @param ex The AttendanceReportCreationException instance.
     * @return ResponseEntity with a 400 status and the exception message.
     */
    @ExceptionHandler(AttendanceReportCreationException.class)
    public ResponseEntity<String> handleAttendanceReportCreationException(AttendanceReportCreationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(ex.getMessage());
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 BAD REQUEST response with validation error messages.
     *
     * @param ex The MethodArgumentNotValidException instance.
     * @return ResponseEntity with a 400 status and a comma-separated list of validation error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> exceptionHandler(MethodArgumentNotValidException ex) {
        List<ObjectError> listOfErrors = ex.getBindingResult().getAllErrors();
        StringBuilder builder = new StringBuilder();
        for (ObjectError error : listOfErrors) {
            String message = error.getDefaultMessage();
            builder.append(message).append(",");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_PLAIN)
                .body(builder.toString());
    }

    /**
     * Handles general Exception and returns a 500 INTERNAL SERVER ERROR response.
     *
     * @param ex The Exception instance.
     * @return ResponseEntity with a 500 status and a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_PLAIN)
                .body("An unexpected error occurred: " + ex.getMessage());
    }
}

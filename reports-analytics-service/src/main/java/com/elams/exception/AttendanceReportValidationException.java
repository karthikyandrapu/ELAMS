package com.elams.exception;

/**
 * Exception thrown when validation of an attendance report fails.
 * This exception extends RuntimeException, meaning it is an unchecked exception.
 */
public class AttendanceReportValidationException extends RuntimeException {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AttendanceReportValidationException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public AttendanceReportValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AttendanceReportValidationException with the specified detail message and cause.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause   The cause (which is saved for later retrieval by the getCause() method).
     */
    public AttendanceReportValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

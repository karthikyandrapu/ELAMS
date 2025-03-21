package com.elams.exception;

/**
 * Exception thrown when an error occurs during the creation of an attendance report.
 * This exception extends RuntimeException, meaning it is an unchecked exception.
 */
public class AttendanceReportCreationException extends RuntimeException {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AttendanceReportCreationException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public AttendanceReportCreationException(String message) {
        super(message);
    }
}

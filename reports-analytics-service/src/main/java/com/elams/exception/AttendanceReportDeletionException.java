package com.elams.exception;

/**
 * Exception thrown when an error occurs during the deletion of an attendance report.
 * This exception extends RuntimeException, meaning it is an unchecked exception.
 */
public class AttendanceReportDeletionException extends RuntimeException {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new AttendanceReportDeletionException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public AttendanceReportDeletionException(String message) {
        super(message);
    }
}

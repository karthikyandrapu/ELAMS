package com.elams.exception;

/**
 * Exception thrown when a method argument fails validation.
 * This exception extends RuntimeException, meaning it is an unchecked exception.
 */
public class MethodArgumentNotValidException extends RuntimeException {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new MethodArgumentNotValidException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public MethodArgumentNotValidException(String message) {
        super(message);
    }
}

package com.elams.exception;

/**
 * Exception thrown when a requested resource is not found.
 * This exception extends RuntimeException, meaning it is an unchecked exception.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Serial version UID for serialization.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the getMessage() method).
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
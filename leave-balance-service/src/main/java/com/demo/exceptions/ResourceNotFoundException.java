package com.demo.exceptions;

/**
 * Custom exception class to represent the scenario where a requested resource is not found.
 * This exception extends {@link RuntimeException}, making it an unchecked exception.
 * It is typically used to indicate that a specific resource, such as a database record,
 * could not be located.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message explaining why the resource was not found.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
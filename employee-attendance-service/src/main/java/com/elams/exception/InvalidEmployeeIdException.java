package com.elams.exception;

import com.elams.aop.AppLogger;
import org.slf4j.Logger;

/**
 * Exception class representing the scenario when an employee ID is invalid.
 * This exception is thrown when an operation receives an employee ID that
 * does not meet the required criteria (e.g., null, negative, or not found).
 */
public class InvalidEmployeeIdException extends RuntimeException {

    private static final Logger logger = AppLogger.getLogger(InvalidEmployeeIdException.class);

    /**
     * Constructs a new InvalidEmployeeIdException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     */
    public InvalidEmployeeIdException(String message) {
        super(message);
        logger.warn("InvalidEmployeeIdException thrown: {}", message);
    }
}
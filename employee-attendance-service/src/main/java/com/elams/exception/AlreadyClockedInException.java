package com.elams.exception;

import com.elams.aop.AppLogger;
import org.slf4j.Logger;

/**
 * Exception class representing the scenario when a clock-in record is not found.
 * This exception is thrown when an operation requires a clock-in record to exist,
 * but it cannot be located.
 */
public class AlreadyClockedInException extends RuntimeException {

    private static final Logger logger = AppLogger.getLogger(AlreadyClockedInException.class);

    /**
     * Constructs a new ClockInNotFoundException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception.
     */
    public AlreadyClockedInException(String message) {
        super(message);
        logger.warn("AlreadyClockedInException thrown: {}", message);
    }
}
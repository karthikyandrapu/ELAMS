package com.elams.aop;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for logging messages throughout the application.
 * Provides a centralized way to create and use loggers, promoting consistency.
 */
public class AppLogger {

    /**
     * Creates a logger instance for the specified class.
     *
     * @param clazz The class for which to create the logger.
     * @return A Logger instance.
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    /**
     * Logs a debug message.
     *
     * @param clazz   The class from which the log message originates.
     * @param message The message to log.
     */
    public static void debug(Class<?> clazz, String message) {
        getLogger(clazz).debug(message);
    }

    /**
     * Logs an info message.
     *
     * @param clazz   The class from which the log message originates.
     * @param message The message to log.
     */
    public static void info(Class<?> clazz, String message) {
        getLogger(clazz).info(message);
    }

    /**
     * Logs a warning message.
     *
     * @param clazz   The class from which the log message originates.
     * @param message The message to log.
     */
    public static void warn(Class<?> clazz, String message) {
        getLogger(clazz).warn(message);
    }

    /**
     * Logs an error message.
     *
     * @param clazz   The class from which the log message originates.
     * @param message The message to log.
     */
    public static void error(Class<?> clazz, String message) {
        getLogger(clazz).error(message);
    }

    /**
     * Logs an error message with an exception.
     *
     * @param clazz     The class from which the log message originates.
     * @param message   The message to log.
     * @param throwable The exception to log.
     */
    public static void error(Class<?> clazz, String message, Throwable throwable) {
        getLogger(clazz).error(message, throwable);
    }
}
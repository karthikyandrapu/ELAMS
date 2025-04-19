package com.elams.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Aspect for logging and monitoring attendance report controller and service methods.
 */
@Aspect
@Component
public class AttendanceReportAspect {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceReportAspect.class);

    /**
     * Logs the entry of controller methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     */
    @Before("execution(* com.elams.controller.AttendanceReportController.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String argsString = (args != null) ? Arrays.toString(args) : "null"; // Check for null
        logger.info("Entering controller method: {} with arguments: {}", joinPoint.getSignature().toShortString(), argsString);
    }

    /**
     * Logs the successful return of controller methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.elams.controller.AttendanceReportController.*(..))", returning = "result")
    public void logAfterReturningController(JoinPoint joinPoint, Object result) {
        logger.info("Exiting controller method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs exceptions thrown by controller methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param exception The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.elams.controller.AttendanceReportController.*(..))", throwing = "exception")
    public void logAfterThrowingController(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in controller method: {} - {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    /**
     * Logs the execution time of controller methods.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method execution.
     * @return The result of the method execution.
     * @throws Throwable If an exception occurs during method execution.
     */
    @Around("execution(* com.elams.controller.AttendanceReportController.*(..))")
    public Object logControllerExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Controller method {} executed in {} ms", joinPoint.getSignature().toShortString(), endTime - startTime);
        return result;
    }

    /**
     * Logs the entry of service methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     */
    @Before("execution(* com.elams.service.AttendanceReportService.*(..))")
    public void logBeforeService(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String argsString = (args != null) ? Arrays.toString(args) : "null"; // Check for null
        logger.info("Entering service method: {} with arguments: {}", joinPoint.getSignature().toShortString(), argsString);
    }

    /**
     * Logs the successful return of service methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.elams.service.AttendanceReportService.*(..))", returning = "result")
    public void logAfterReturningService(JoinPoint joinPoint, Object result) {
        logger.info("Exiting service method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs exceptions thrown by service methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param exception The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.elams.service.AttendanceReportService.*(..))", throwing = "exception")
    public void logAfterThrowingService(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in service method: {} - {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    /**
     * Logs the execution time of service methods.
     *
     * @param joinPoint The ProceedingJoinPoint representing the method execution.
     * @return The result of the method execution.
     * @throws Throwable If an exception occurs during method execution.
     */
    @Around("execution(* com.elams.service.AttendanceReportService.*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Service method {} executed in {} ms", joinPoint.getSignature().toShortString(), endTime - startTime);
        return result;
    }
}
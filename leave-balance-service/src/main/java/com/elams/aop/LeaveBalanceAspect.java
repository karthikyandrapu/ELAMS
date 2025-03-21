package com.elams.aop;

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

import java.util.Arrays;

/**
 * Aspect class for logging and monitoring Leave Balance related operations.
 * This class uses AspectJ annotations to intercept method executions in the controller and service layers.
 * It provides logging for method entry, exit, exceptions, and execution time.
 */
@Aspect
@Component
public class LeaveBalanceAspect {

    private static final Logger logger = LoggerFactory.getLogger(LeaveBalanceAspect.class);

    /**
     * Logs the entry of a method in the LeaveBalanceController.
     *
     * @param joinPoint The join point representing the method execution.
     */
    @Before("execution(* com.elams.controllers.LeaveBalanceController.*(..))")
    public void logBeforeController(JoinPoint joinPoint) {
        logger.info("Entering controller method: {} with arguments: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Logs the exit of a method in the LeaveBalanceController with the returned result.
     *
     * @param joinPoint The join point representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.elams.controllers.LeaveBalanceController.*(..))", returning = "result")
    public void logAfterReturningController(JoinPoint joinPoint, Object result) {
        logger.info("Exiting controller method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs the occurrence of an exception in a method of the LeaveBalanceController.
     *
     * @param joinPoint The join point representing the method execution.
     * @param exception The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.elams.controllers.LeaveBalanceController.*(..))", throwing = "exception")
    public void logAfterThrowingController(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in controller method: {} - {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    /**
     * Logs the entry of a method in the LeaveBalanceServiceImpl.
     *
     * @param joinPoint The join point representing the method execution.
     */
    @Before("execution(* com.elams.service.LeaveBalanceServiceImpl.*(..))")
    public void logBeforeService(JoinPoint joinPoint) {
        logger.info("Entering service method: {} with arguments: {}", joinPoint.getSignature().toShortString(), Arrays.toString(joinPoint.getArgs()));
    }

    /**
     * Logs the exit of a method in the LeaveBalanceServiceImpl with the returned result.
     *
     * @param joinPoint The join point representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.elams.service.LeaveBalanceServiceImpl.*(..))", returning = "result")
    public void logAfterReturningService(JoinPoint joinPoint, Object result) {
        logger.info("Exiting service method: {} with result: {}", joinPoint.getSignature().toShortString(), result);
    }

    /**
     * Logs the occurrence of an exception in a method of the LeaveBalanceServiceImpl.
     *
     * @param joinPoint The join point representing the method execution.
     * @param exception The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.elams.service.LeaveBalanceServiceImpl.*(..))", throwing = "exception")
    public void logAfterThrowingService(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception in service method: {} - {}", joinPoint.getSignature().toShortString(), exception.getMessage(), exception);
    }

    /**
     * Logs the execution time of methods in the AttendanceReportController.
     *
     * @param joinPoint The proceeding join point representing the method execution.
     * @return The result of the method execution.
     * @throws Throwable If an exception occurs during method execution.
     */
    @Around("execution(* com.elams.controller.AttendanceReportController.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        logger.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), endTime - startTime);
        return result;
    }
}
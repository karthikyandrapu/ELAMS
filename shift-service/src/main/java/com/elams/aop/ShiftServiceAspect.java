package com.elams.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * Aspect class for logging method executions in the Shift Service.
 * This class uses AspectJ annotations to intercept method calls in service, controller, and repository layers,
 * and logs method entry, exit, and exceptions.
 */
@Component
@Aspect
@Slf4j
public class ShiftServiceAspect {

	/**
     * Pointcut definition for service layer methods.
     */
    @Pointcut("execution(* com.elams.service..*(..))")
    public void serviceMethods() {
    }

    /**
     * Pointcut definition for controller layer methods.
     */
    @Pointcut("execution(* com.elams.controller..*(..))")
    public void controllerMethods() {
    }

    /**
     * Pointcut definition for repository layer methods.
     */
    @Pointcut("execution(* com.elams.repository..*(..))")
    public void repositoryMethods() {
    }
   
    
    /**
     * Logs method entry before executing service layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     */
    @Before("serviceMethods()")
    public void logBeforeServiceMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("Service Method {} is called with arguments: {}", methodName, methodArgs);
    }
   
    /**
     * Logs method exit after successful execution of service layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     */
    @AfterReturning(pointcut = "serviceMethods()")
    public void logAfterServiceMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Service Method {} has returned.", methodName);
    }
    
    /**
     * Logs method exit after throwing an exception in service layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param exception The Throwable exception thrown by the method.
     */
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "exception")
    public void logAfterServiceMethodThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Service Method {} has thrown an Exception -> {}", methodName, exception.getMessage());
    }
   
    /**
     * Logs method entry before executing controller layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     */
    @Before("controllerMethods()")
    public void logBeforeControllerMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("Controller Method {} is called with arguments: {}", methodName, methodArgs);
    }
   
    /**
     * Logs method exit after successful execution of controller layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterControllerMethodExecution(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Controller Method {} has returned.", methodName);
    }
    
    /**
     * Logs method exit after throwing an exception in controller layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param exception The Throwable exception thrown by the method.
     */
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
    public void logAfterControllerMethodThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Controller Method {} has thrown an Exception -> {}", methodName, exception.getMessage());
    }

    /**
     * Logs method entry before executing repository layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     */
    @Before("repositoryMethods()")
    public void logBeforeRepositoryMethodExecution(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        log.info("Repository Method {} is called with arguments: {}", methodName, methodArgs);
    }

    /**
     * Logs method exit after successful execution of repository layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "repositoryMethods()", returning = "result")
    public void logAfterRepositoryMethodExecution(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Repository Method {} has returned.", methodName);
    }

    /**
     * Logs method exit after throwing an exception in repository layer methods.
     *
     * @param joinPoint The JoinPoint representing the method execution.
     * @param exception The Throwable exception thrown by the method.
     */
    @AfterThrowing(pointcut = "repositoryMethods()", throwing = "exception")
    public void logAfterRepositoryMethodThrowing(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Repository Method {} has thrown an Exception -> {}", methodName, exception.getMessage());
    }
}
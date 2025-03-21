package com.elams.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Annotation for validating that an attendance report is unique.
 * This annotation is used to ensure that an attendance report with the same
 * employee ID and date range does not already exist.
 */
@Documented
@Constraint(validatedBy = UniqueAttendanceReportValidator.class)
@Target({ElementType.TYPE}) // Apply to the entire class, not just a single field
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueAttendanceReport {

    /**
     * The error message to be returned if the validation fails.
     *
     * @return The error message.
     */
    String message() default "Attendance report with these details already exists.";

    /**
     * The groups the constraint belongs to.
     *
     * @return An array of classes representing the groups.
     */
    Class<?>[] groups() default {};

    /**
     * The payload associated with the constraint.
     *
     * @return An array of classes representing the payload.
     */
    Class<? extends Payload>[] payload() default {};
}

package com.elams.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object (DTO) representing employee attendance information.
 * This class encapsulates the attendance details of an employee, including their
 * employee ID, clock-in time, clock-out time, and calculated work hours.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {

    /**
     * The unique identifier of the employee.
     * This field is mandatory and cannot be null.
     */
    @NotNull
    private Long employeeId;

    /**
     * The date and time when the employee clocked in.
     * Represents the start of the employee's work period.
     */
    private LocalDateTime clockInTime;

    /**
     * The date and time when the employee clocked out.
     * Represents the end of the employee's work period.
     */
    private LocalDateTime clockOutTime;

    /**
     * The total number of work hours calculated based on the clock-in and clock-out times.
     */
    private Double workHours;
}
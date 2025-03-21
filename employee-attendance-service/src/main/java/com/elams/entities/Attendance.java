package com.elams.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing employee attendance records.
 * This class maps to the "Attendance" table in the database and stores
 * information about employee clock-in, clock-out, and work hours.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    /**
     * The unique identifier for the attendance record.
     * Automatically generated by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the employee associated with this attendance record.
     * Maps to the "employee_id" column in the database.
     */
    
    private Long employeeId;

    /**
     * The date and time when the employee clocked in.
     */
    private LocalDateTime clockInTime;

    /**
     * The date and time when the employee clocked out.
     */
    private LocalDateTime clockOutTime;

    /**
     * The total calculated work hours for the employee.
     */
    private Double workHours;
}
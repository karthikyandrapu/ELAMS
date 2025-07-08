package com.elams.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing attendance information.
 * This DTO is used to transfer attendance data between different layers of the application.
 */
@Data
@NoArgsConstructor
public class AttendanceDTO {

    /**
     * The unique identifier for the attendance record.
     */
    private Long attendanceId;

    /**
     * The identifier of the employee associated with the attendance record.
     */
    private Long employeeId;

    /**
     * The time when the employee clocked in.
     */
    private LocalDateTime clockInTime;

    /**
     * The time when the employee clocked out.
     */
    private LocalDateTime clockOutTime;

    /**
     * The total work hours for the attendance record.
     */
    private float workHours;
}

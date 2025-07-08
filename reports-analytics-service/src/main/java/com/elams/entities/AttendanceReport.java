package com.elams.entities;








import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity representing an attendance report.
 * This entity stores attendance report data, including employee details,
 * date range, total attendance, and absenteeism.
 */
@Entity
@Data
@NoArgsConstructor
public class AttendanceReport {

    /**
     * The unique identifier for the attendance report.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    /**
     * The identifier of the employee associated with the attendance report.
     * Cannot be null.
     */
    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    /**
     * The date range for which the attendance report is generated.
     * Cannot be null.
     */
    @NotNull(message = "Date range cannot be null")
    private String dateRange;

    /**
     * The total attendance count for the specified date range.
     * Cannot be negative.
     */
    @Min(value = 0, message = "Total attendance cannot be negative")
    private Integer totalAttendance;

    /**
     * The absenteeism count for the specified date range.
     * Cannot be negative.
     */
    @Min(value = 0, message = "Absenteeism cannot be negative")
    private Integer absenteeism;
}


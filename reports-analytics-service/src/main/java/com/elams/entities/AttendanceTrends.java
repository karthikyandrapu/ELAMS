package com.elams.entities;


import jakarta.persistence.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing attendance trends.
 * This entity stores attendance trend data, including employee details,
 * date range, average attendance and absenteeism percentages, and the overall trend.
 */
@Entity
@Data
@NoArgsConstructor

public class AttendanceTrends {

    /**
     * The unique identifier for the attendance trend record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trendId;

    /**
     * The identifier of the employee associated with the attendance trend.
     */
    private Long employeeId;

    /**
     * The date range for which the attendance trend is calculated.
     */
    private String dateRange;

    /**
     * The average attendance percentage for the specified date range.
     */
    private double averageAttendancePercentage;

    /**
     * The average absenteeism percentage for the specified date range.
     */
    private double averageAbsenteeismPercentage;

    /**
     * The overall trend (e.g., "Improving", "Declining", "Stable") for the specified date range.
     */
    private String overallTrend;
}
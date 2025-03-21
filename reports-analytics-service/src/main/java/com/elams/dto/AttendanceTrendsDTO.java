package com.elams.dto;


import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing attendance trends.
 * This DTO encapsulates attendance trend data for transfer between layers.
 * It includes employee details, date range, average attendance and absenteeism percentages, and the overall trend.
 */
@Getter
@Setter
@NoArgsConstructor
@Data
public class AttendanceTrendsDTO {

    /**
     * The unique identifier for the attendance trend record.
     */
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
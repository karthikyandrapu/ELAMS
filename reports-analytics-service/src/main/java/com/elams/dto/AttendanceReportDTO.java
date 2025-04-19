package com.elams.dto;

import java.util.List;

import java.util.Map;

import org.springframework.validation.annotation.Validated;

import com.elams.entities.LeaveType;
import com.elams.entities.Shift;
import com.elams.validation.UniqueAttendanceReport;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing an attendance report.
 * This DTO encapsulates attendance report data for transfer between layers.
 * It includes employee details, date range, attendance metrics, shifts, and leave balances.
 */
@Getter
@Setter
@NoArgsConstructor
@Data
@UniqueAttendanceReport
@Validated
public class AttendanceReportDTO {

    /**
     * The unique identifier for the attendance report.
     */
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

    /**
     * The list of shifts included in the attendance report.
     */
    private List<Shift> shifts;

    /**
     * The map of leave type to leave balances for the employee.
     */
    private Map<LeaveType, Double> leaveBalances;
}


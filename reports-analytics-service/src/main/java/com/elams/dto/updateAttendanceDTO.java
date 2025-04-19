package com.elams.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the data required to update attendance information.
 * This DTO is used to transfer update attendance data between layers,
 * including total attendance, absenteeism, and employee ID.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class updateAttendanceDTO {

    /**
     * The updated total attendance count.
     * Cannot be negative.
     */
    @Min(value = 0, message = "Total attendance cannot be negative")
    private int totalAttendance;

    /**
     * The updated absenteeism count.
     * Cannot be negative.
     */
    @Min(value = 0, message = "Absenteeism cannot be negative")
    private int absenteeism;

    /**
     * The identifier of the employee whose attendance is being updated.
     * Cannot be null.
     */
    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;
}
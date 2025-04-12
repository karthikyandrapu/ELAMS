package com.elams.dto;


import com.elams.entities.LeaveType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Data Transfer Object (DTO) representing an employee's leave balance.
 * This DTO is used to transfer leave balance information between layers,
 * including employee ID, leave type, and balance.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {

    /**
     * The unique identifier for the leave balance record.
     */
    private Long id;

    /**
     * The identifier of the employee.
     * Cannot be null.
     */
    @NotNull(message = "Employee ID Should not be null")
    Long employeeId;

    /**
     * The type of leave (e.g., SICK, VACATION, CASUAL).
     * Cannot be null.
     */
    @NotNull(message = "Leave Type Should Be Specified")
    private LeaveType leaveType;

    /**
     * The leave balance for the employee.
     * Cannot be null and must be greater than or equal to zero.
     */
    @NotNull(message = "Balance should not be null")
    @PositiveOrZero(message = "Balance should be greater than or equal to zero")
    private Double balance;
}
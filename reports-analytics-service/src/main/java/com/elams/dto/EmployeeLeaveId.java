package com.elams.dto;

import com.elams.entities.LeaveType;

import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Data Transfer Object (DTO) representing an employee's leave ID and balance.
 * This DTO is used to transfer employee leave information, including ID, balance, and leave type.
 */
@Data
@NoArgsConstructor
public class EmployeeLeaveId {

    /**
     * The identifier of the employee.
     */
    private Long employeeId;

    /**
     * The leave balance for the employee.
     */
    private Double balance;

    /**
     * The type of leave (e.g., SICK, VACATION, CASUAL).
     */
    private LeaveType leaveType;
}
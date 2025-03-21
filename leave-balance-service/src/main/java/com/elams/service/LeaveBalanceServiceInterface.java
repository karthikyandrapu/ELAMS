package com.elams.service;

import java.util.List;

import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveType;

/**
 * Service interface for managing leave balance operations.
 * This interface defines methods for retrieving, updating, creating, and deleting leave balance records.
 */
public interface LeaveBalanceServiceInterface {

    /**
     * Retrieves the leave balance for a specific employee and leave type.
     *
     * @param empId     The ID of the employee.
     * @param leaveType The type of leave.
     * @return The leave balance DTO.
     */
    LeaveBalanceDTO getLeaveBalance(Long empId, LeaveType leaveType);

    /**
     * Updates the leave balance for an employee after taking leave.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @param daysTaken  The number of days taken.
     * @return The updated leave balance DTO.
     */
    LeaveBalanceDTO updateLeaveBalance(Long employeeId, LeaveType leaveType, Double daysTaken);

    /**
     * Creates a new leave balance record for an employee.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @param balance    The initial leave balance.
     * @return The created leave balance DTO.
     */
    LeaveBalanceDTO createLeaveBalance(Long employeeId, LeaveType leaveType, Double balance);

    /**
     * Retrieves all leave balances for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A list of leave balance DTOs.
     */
    List<LeaveBalanceDTO> getAllLeaveBalanceOfEmployee(Long employeeId);

    /**
     * Checks if an employee has sufficient leave balance for the requested leave.
     *
     * @param empId     The ID of the employee.
     * @param leaveType The type of leave.
     * @param balance   The number of days requested.
     * @return true if the employee has sufficient balance, false otherwise.
     */
    boolean hasSufficientBalance(Long empId, LeaveType leaveType, Double balance);

    /**
     * Deletes all leave balances for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return true if the deletion is successful.
     */
    boolean deleteAllLeaveBalancesForEmployee(Long employeeId);
}
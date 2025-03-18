package com.elams.service;

import com.elams.dtos.ShiftDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Service interface for managing employee shifts.
 * This interface defines methods for assigning, retrieving, updating, and deleting shifts,
 * as well as handling shift swap requests and viewing colleague shifts.
 */
public interface ShiftService {
	 /**
     * Assigns a shift to an employee.
     *
     * @param shiftDTO The {@link ShiftDTO} containing the shift details.
     * @param managerId The ID of the manager assigning the shift.
     * @return The assigned {@link ShiftDTO}.
     */
	ShiftDTO assignShift(ShiftDTO shiftDTO, Long managerId);
	
	/**
     * Retrieves all shifts assigned to a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A list of {@link ShiftDTO} representing the employee's shifts.
     */
    List<ShiftDTO> viewEmployeeShifts(Long employeeId);
    
    /**
     * Retrieves all shifts managed by a specific manager.
     *
     * @param managerId The ID of the manager.
     * @return A list of {@link ShiftDTO} representing all shifts under the manager.
     */
    List<ShiftDTO> viewManagerShifts(Long managerId);
    
    /**
     * Retrieves all shifts assigned to a specific manager, acting as an employee.
     *
     * @param managerId The ID of the manager.
     * @return A list of {@link ShiftDTO} representing the manager's own shifts.
     */
    List<ShiftDTO> viewManagerOwnShifts(Long managerId);
    
    /**
     * Requests a shift swap between two employees.
     *
     * @param employeeId The ID of the employee requesting the swap.
     * @param shiftId The ID of the shift to be swapped.
     * @param swapWithEmployeeId The ID of the employee to swap with.
     * @return The updated {@link ShiftDTO} representing the shift swap request.
     */
    ShiftDTO requestShiftSwap(Long employeeId, Long shiftId, Long swapWithEmployeeId);
    
    /**
     * Approves a shift swap request.
     *
     * @param managerId The ID of the manager approving the swap.
     * @param shiftId The ID of the shift being swapped.
     * @param newEmployeeId The ID of the employee taking the shift.
     * @return The updated {@link ShiftDTO} representing the approved shift swap.
     */
    ShiftDTO approveShiftSwap(Long managerId, Long shiftId, Long newEmployeeId);
    
    /**
     * Rejects a shift swap request.
     *
     * @param managerId The ID of the manager rejecting the swap.
     * @param shiftId The ID of the shift being swapped.
     * @return The updated {@link ShiftDTO} representing the rejected shift swap.
     */
    ShiftDTO rejectShiftSwap(Long managerId, Long shiftId); 
    
    /**
     * Updates an existing shift.
     *
     * @param managerId The ID of the manager updating the shift.
     * @param shiftId The ID of the shift to update.
     * @param newDate The new date of the shift.
     * @param newTime The new time of the shift.
     * @return The updated {@link ShiftDTO}.
     */
    ShiftDTO updateShift(Long managerId, Long shiftId, LocalDate newDate, LocalTime newTime);
    
    /**
     * Deletes a shift.
     *
     * @param managerId The ID of the manager deleting the shift.
     * @param shiftId The ID of the shift to delete.
     */
    void deleteShift(Long managerId, Long shiftId);
    
    /**
     * Retrieves the shifts of an employee's colleagues on a specific date.
     *
     * @param employeeId The ID of the employee whose colleagues' shifts are requested.
     * @param shiftDate The date of the shifts.
     * @return A list of {@link ShiftDTO} representing the colleague's shifts.
     */
    List<ShiftDTO> getColleagueShifts(Long employeeId, LocalDate shiftDate);
    
    /**
     * Retrieves all shifts of a specific employee under a specific manager.
     *
     * @param managerId The ID of the manager.
     * @param employeeId The ID of the employee.
     * @return A list of {@link ShiftDTO} representing the employee's shifts under the manager.
     */
    List<ShiftDTO> viewManagerEmployeeShifts(Long managerId, Long employeeId);
    
    /**
     * Retrieves all pending swap requests for a manager.
     *
     * @param managerId The ID of the manager.
     * @return A list of {@link ShiftDTO} representing the pending swap requests.
     */
	List<ShiftDTO> getManagerSwapRequests(Long managerId);
}
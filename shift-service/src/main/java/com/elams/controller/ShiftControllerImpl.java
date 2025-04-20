package com.elams.controller;

import com.elams.dtos.ShiftDTO;
import com.elams.entities.Shift;
import com.elams.enums.EmployeeRole;
import com.elams.service.ShiftService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for managing employee shifts.
 * This class provides endpoints for assigning, retrieving, updating, and deleting shifts,
 * as well as handling shift swap requests and viewing colleague shifts.
 * It also includes role-based authorization to ensure that only managers can perform certain actions.
 */
@RestController
@RequestMapping("/shifts")
public class ShiftControllerImpl implements ShiftController {

    private final ShiftService shiftService;

    public ShiftControllerImpl(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    /**
     * Assigns a shift to an employee if the requester has the MANAGER role.
     * <p>
     * Endpoint: {@code POST /shifts/assign}.
     *
     * @param managerId  ID of the manager to assign the shift.
     * @param shiftDTO   Shift data transfer object containing shift details.
     * @param role       Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role isn't MANAGER,
     * {@code HTTP 200 OK} with the assigned {@link ShiftDTO} if successful.
     */
    @PostMapping("/assign")
    public ResponseEntity<ShiftDTO> assignShift(
            @RequestParam Long managerId,
            @RequestBody ShiftDTO shiftDTO,
            @RequestHeader("role") EmployeeRole role) {

        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ShiftDTO assignedShift = shiftService.assignShift(shiftDTO, managerId);
        return ResponseEntity.ok(assignedShift);
    }

    /**
     * Retrieves the list of shifts assigned to a specific employee.
     * <p>
     * Endpoint: {@code GET /shifts/employee/{employeeId}}.
     *
     * @param employeeId ID of the employee whose shifts are being retrieved.
     * @return {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the employee's shifts.
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<ShiftDTO>> getEmployeeShifts(@PathVariable Long employeeId) {
        return ResponseEntity.ok(shiftService.viewEmployeeShifts(employeeId));
    }

    /**
     * Retrieves a list of shifts managed by a specific manager.
     * <p>
     * Endpoint: {@code GET /shifts/manager/{managerId}/employees}.
     *
     * @param managerId ID of the manager whose shifts are being retrieved.
     * @param role      Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the manager's shifts.
     */
    @GetMapping("/manager/{managerId}/employees")
    public ResponseEntity<List<ShiftDTO>> viewManagerShifts(
            @PathVariable Long managerId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ShiftDTO> shifts = shiftService.viewManagerShifts(managerId);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Fetches the list of shifts specifically assigned to a manager.
     * <p>
     * Endpoint: {@code GET /shifts/manager/{managerId}}.
     *
     * @param managerId ID of the manager whose shifts are being retrieved.
     * @param role      Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the manager's assigned shifts.
     */
    @GetMapping("/manager/{managerId}")
    public ResponseEntity<List<ShiftDTO>> viewManagerOwnShifts(
            @PathVariable Long managerId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ShiftDTO> shifts = shiftService.viewManagerOwnShifts(managerId);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Handles requests for shift swaps between employees.
     * <p>
     * Endpoint: {@code POST /shifts/swap/request}.
     *
     * @param employeeId         ID of the employee requesting the shift swap.
     * @param shiftId            ID of the shift to be swapped.
     * @param swapWithEmployeeId ID of the employee whose shift is being requested.
     * @param role               Requester's role (must be EMPLOYEE).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not EMPLOYEE,
     * {@code HTTP 200 OK} with the {@link ShiftDTO} of the requested shift swap if successful.
     */
    @PostMapping("/swap/request")
    public ResponseEntity<ShiftDTO> requestShiftSwap(
            @RequestParam Long employeeId,
            @RequestParam Long shiftId,
            @RequestParam Long swapWithEmployeeId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ShiftDTO shiftDTO = shiftService.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId);
        return ResponseEntity.ok(shiftDTO);
    }

    /**
     * Approves a shift swap request for a specific shift.
     * <p>
     * Endpoint: {@code POST /shifts/swap/{shiftId}/approve}.
     *
     * @param shiftId            ID of the shift for which the swap is being approved.
     * @param managerId          ID of the manager approving the shift swap.
     * @param swapWithEmployeeId ID of the employee involved in the swap.
     * @param role               Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with the {@link ShiftDTO} object representing the approved shift swap.
     */
    @PostMapping("/swap/{shiftId}/approve")
    public ResponseEntity<ShiftDTO> approveShiftSwap(
            @PathVariable Long shiftId,
            @RequestParam Long managerId,
            @RequestParam Long swapWithEmployeeId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ShiftDTO shiftDTO = shiftService.approveShiftSwap(managerId, shiftId, swapWithEmployeeId);
        return ResponseEntity.ok(shiftDTO);
    }

    /**
     * Rejects a shift swap request for a specific shift.
     * <p>
     * Endpoint: {@code POST /shifts/swap/{shiftId}/reject}.
     *
     * @param shiftId   ID of the shift for which the swap is being rejected.
     * @param managerId ID of the manager rejecting the shift swap.
     * @param role      Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with the {@link ShiftDTO} object representing the rejected shift swap.
     */
    @PostMapping("/swap/{shiftId}/reject")
    public ResponseEntity<ShiftDTO> rejectShiftSwap(
            @PathVariable Long shiftId,
            @RequestParam Long managerId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ShiftDTO shiftDTO = shiftService.rejectShiftSwap(managerId, shiftId);
        return ResponseEntity.ok(shiftDTO);
    }

    /**
     * Updates the details of an existing shift.
     * <p>
     * Endpoint: {@code PUT /shifts/{shiftId}/update}.
     *
     * @param shiftId   ID of the shift to be updated.
     * @param managerId ID of the manager performing the update.
     * @param shift     The new shift details, including date and time.
     * @param role      Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with the updated {@link ShiftDTO} object if the operation is successful.
     */
    @PutMapping("/{shiftId}/update")
    public ResponseEntity<ShiftDTO> updateShift(
            @PathVariable Long shiftId,
            @RequestParam Long managerId,
            @RequestBody Shift shift,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ShiftDTO updatedShift = shiftService.updateShift(managerId, shiftId, shift.getShiftDate(), shift.getShiftTime());
        return ResponseEntity.ok(updatedShift);
    }

    /**
     * Deletes an existing shift based on the provided shift ID.
     * <p>
     * Endpoint: {@code DELETE /shifts/{shiftId}/delete}.
     *
     * @param shiftId   ID of the shift to be deleted.
     * @param managerId ID of the manager performing the deletion.
     * @param role      Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 204 NO CONTENT} if the shift is successfully deleted.
     */
    @DeleteMapping("/{shiftId}/delete")
    public ResponseEntity<Void> deleteShift(
            @PathVariable Long shiftId,
            @RequestParam Long managerId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        shiftService.deleteShift(managerId, shiftId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the shifts of colleagues for a specific employee on a given date.
     * <p>
     * Endpoint: {@code GET /shifts/colleagues/{employeeId}}.
     *
     * @param employeeId ID of the employee requesting colleague shifts.
     * @param shiftDate  The date for which colleague shifts are being retrieved.
     * @param role       Requester's role.
     * @return {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the colleagues' shifts.
     */
    @GetMapping("/colleagues/{employeeId}")
    public ResponseEntity<List<ShiftDTO>> getColleagueShifts(
            @PathVariable Long employeeId,
            @RequestParam LocalDate shiftDate,
            @RequestHeader("role") EmployeeRole role) {
        List<ShiftDTO> colleagueShifts = shiftService.getColleagueShifts(employeeId, shiftDate);
        return ResponseEntity.ok(colleagueShifts);
    }

    /**
     * Retrieves the list of shifts assigned to a specific employee under a specific manager.
     * <p>
     * Endpoint: {@code GET /shifts/manager/{managerId}/employee/{employeeId}}.
     *
     * @param managerId  ID of the manager to verify the relationship.
     * @param employeeId ID of the employee whose shifts are being retrieved.
     * @param role       Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the employee's shifts under the manager.
     */
    @GetMapping("/manager/{managerId}/employee/{employeeId}")
    public ResponseEntity<List<ShiftDTO>> viewManagerEmployeeShifts(
            @PathVariable Long managerId,
            @PathVariable Long employeeId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ShiftDTO> shifts = shiftService.viewManagerEmployeeShifts(managerId, employeeId);
        return ResponseEntity.ok(shifts);
    }

    /**
     * Retrieves a list of shift swap requests for a specific manager.
     * <p>
     * Endpoint: {@code GET /shifts/manager/{managerId}/swap-requests}.
     *
     * @param managerId ID of the manager whose swap requests are being retrieved.
     * @param role      Requester's role (must be MANAGER).
     * @return {@code HTTP 403 FORBIDDEN} if the role is not MANAGER,
     * {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the shift swap requests.
     */
    @GetMapping("/manager/{managerId}/swap-requests")
    public ResponseEntity<List<ShiftDTO>> getManagerSwapRequests(
            @PathVariable Long managerId,
            @RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ShiftDTO> requests = shiftService.getManagerSwapRequests(managerId);
        return ResponseEntity.ok(requests);
    }
    
    /**
     * Retrieves the list of upcoming shifts for a specific employee.
     * <p>
     * Endpoint: {@code GET /shifts/employee/{employeeId}/upcoming}.
     *
     * @param employeeId ID of the employee whose upcoming shifts are being retrieved.
     * @return {@code HTTP 200 OK} with a List of {@link ShiftDTO} objects representing the employee's upcoming shifts.
     */
    @GetMapping("/employee/{employeeId}/upcoming")
    public ResponseEntity<List<ShiftDTO>> getUpcomingEmployeeShifts(
    		@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role) {
        if (role != EmployeeRole.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ShiftDTO> upcomingShifts = shiftService.viewUpcomingEmployeeShifts(employeeId);
        return ResponseEntity.ok(upcomingShifts);
    }
    
    /**
     * Retrieves all swap requests made by a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @param role       The role of the employee.
     * @return {@code HTTP 200 OK} with a list of {@link ShiftDTO} objects representing the swap requests.
     *         {@code HTTP 403 Forbidden} if the role is not EMPLOYEE.
     */
    @GetMapping("/employee/{employeeId}/swap/requests")
    public ResponseEntity<List<ShiftDTO>> viewEmployeeSwapRequests(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role) {
    	 if (role != EmployeeRole.EMPLOYEE) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
         }
        List<ShiftDTO> swapRequests = shiftService.viewEmployeeSwapRequests(employeeId);
        return ResponseEntity.ok(swapRequests);
    }

    /**
     * Retrieves all rejected swap requests for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @param role       The role of the employee.
     * @return {@code HTTP 200 OK} with a list of {@link ShiftDTO} objects representing the rejected swap requests.
     *         {@code HTTP 403 Forbidden} if the role is not EMPLOYEE.
     */
    @GetMapping("/employee/{employeeId}/swap/rejected")
    public ResponseEntity<List<ShiftDTO>> viewEmployeeRejectedSwapRequests(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role) {
    	 if (role != EmployeeRole.EMPLOYEE) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
         }
        List<ShiftDTO> rejectedRequests = shiftService.viewEmployeeRejectedSwapRequests(employeeId);
        return ResponseEntity.ok(rejectedRequests);
    }

    /**
     * Retrieves all approved swap requests for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @param role       The role of the employee.
     * @return {@code HTTP 200 OK} with a list of {@link ShiftDTO} objects representing the approved swap requests.
     *         {@code HTTP 403 Forbidden} if the role is not EMPLOYEE.
     */
    @GetMapping("/employee/{employeeId}/swap/approved")
    public ResponseEntity<List<ShiftDTO>> viewEmployeeApprovedSwapRequests(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role) {
    	 if (role != EmployeeRole.EMPLOYEE) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
         }
        List<ShiftDTO> approvedRequests = shiftService.viewEmployeeApprovedSwapRequests(employeeId);
        return  ResponseEntity.ok(approvedRequests);
    }
    
    /**
     * Retrieves all shifts swapped with another employee for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @param role       The role of the employee.
     * @return {@code HTTP 200 OK} with a list of {@link ShiftDTO} objects representing the shifts swapped with another employee.
     *         {@code HTTP 403 Forbidden} if the role is not EMPLOYEE.
     */
    @GetMapping("/employee/{employeeId}/swap/anotheremployee")
    public ResponseEntity<List<ShiftDTO>> viewEmployeeSwappedWithAnotherEmployee(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role) {
    	 if (role != EmployeeRole.EMPLOYEE) {
             return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
         }
        List<ShiftDTO> swappedWithAnotherEmployee = shiftService.viewEmployeeSwappedWithAnotherEmployee(employeeId);
        return  ResponseEntity.ok(swappedWithAnotherEmployee);
    }
    
}
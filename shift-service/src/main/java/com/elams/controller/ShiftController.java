package com.elams.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import com.elams.dtos.ShiftDTO;
import com.elams.entities.Shift;
import com.elams.enums.EmployeeRole;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller interface for managing employee shifts.
 * This interface defines endpoints for assigning, retrieving, updating, and deleting shifts,
 * as well as handling shift swap requests and viewing colleague shifts.
 */
@Tag(name = "Shift Management", description = "Endpoints for managing employee shifts")
@CrossOrigin(origins = "http://localhost:4200")
public interface ShiftController {

	 /**
     * Assigns a shift to an employee.
     */
	@Operation(summary = "Assign a shift to an employee", description = "Assigns a new shift to an employee by a manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shift assigned successfully",           content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
	@PostMapping("/assign")
    ResponseEntity<ShiftDTO> assignShift(
            @Parameter(description = "Manager ID", required = true) @RequestParam Long managerId,
            @Parameter(description = "Shift details", required = true) @Valid @RequestBody ShiftDTO shiftDTO,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

	 /**
     * Retrieves all shifts assigned to a specific employee.
     */
    @Operation(summary = "Get shifts for an employee", description = "Retrieves all shifts assigned to a specific employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employee shifts",               content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/employee/{employeeId}")
    ResponseEntity<List<ShiftDTO>> getEmployeeShifts(
            @Parameter(description = "Employee ID", required = true) @PathVariable Long employeeId);

    /**
     * Retrieves all shifts managed by a specific manager.
     */
    @Operation(summary = "View shifts managed by a manager", description = "Retrieves all shifts managed by a specific manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of manager shifts",                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/manager/{managerId}/employees")
    ResponseEntity<List<ShiftDTO>> viewManagerShifts(
            @Parameter(description = "Manager ID", required = true) @PathVariable Long managerId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Retrieves all shifts assigned to a specific manager.
     */
    @Operation(summary = "View own shifts of a manager", description = "Retrieves all shifts assigned to a specific manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of manager own shifts", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/manager/{managerId}")
    ResponseEntity<List<ShiftDTO>> viewManagerOwnShifts(
            @Parameter(description = "Manager ID", required = true) @PathVariable Long managerId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Requests a shift swap between two employees.
     */
    @Operation(summary = "Request a shift swap", description = "Requests a shift swap between two employees.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shift swap requested successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Shift or employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/swap/request")
    ResponseEntity<ShiftDTO> requestShiftSwap(
            @Parameter(description = "Employee ID requesting swap", required = true) @RequestParam Long employeeId,
            @Parameter(description = "Shift ID to be swapped", required = true) @RequestParam Long shiftId,
            @Parameter(description = "Employee ID to swap with", required = true) @RequestParam Long swapWithEmployeeId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Approves a requested shift swap.
     */
    @Operation(summary = "Approve a shift swap", description = "Approves a requested shift swap by a manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shift swap approved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Shift or manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/swap/{shiftId}/approve")
    ResponseEntity<ShiftDTO> approveShiftSwap(
            @Parameter(description = "Shift ID to be approved", required = true) @PathVariable Long shiftId,
            @Parameter(description = "Manager ID approving swap", required = true) @RequestParam Long managerId,
            @Parameter(description = "Employee ID to swap with", required = true) @RequestParam Long swapWithEmployeeId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);
    
    /**
     * Rejects a requested shift swap.
     */
    @Operation(summary = "Reject a shift swap", description = "Rejects a requested shift swap by a manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shift swap rejected successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Shift or manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/swap/{shiftId}/reject")
    ResponseEntity<ShiftDTO> rejectShiftSwap(
            @Parameter(description = "Shift ID to be rejected", required = true) @PathVariable Long shiftId,
            @Parameter(description = "Manager ID rejecting swap", required = true) @RequestParam Long managerId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);
	 
    /**
     * Updates an existing shift.
     */
    @Operation(summary = "Update a shift", description = "Updates an existing shift by a manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Shift updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Shift or manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{shiftId}/update")
    ResponseEntity<ShiftDTO> updateShift(
            @Parameter(description = "Shift ID to be updated", required = true) @PathVariable Long shiftId,
            @Parameter(description = "Manager ID updating shift", required = true) @RequestParam Long managerId,
            @Parameter(description = "Updated shift details", required = true) @Valid @RequestBody Shift shift,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Deletes an existing shift.
     */
    @Operation(summary = "Delete a shift", description = "Deletes an existing shift by a manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Shift deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Shift or manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{shiftId}/delete")
    ResponseEntity<Void> deleteShift(
            @Parameter(description = "Shift ID to be deleted", required = true) @PathVariable Long shiftId,
            @Parameter(description = "Manager ID deleting shift", required = true) @RequestParam Long managerId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Retrieves shifts of colleagues on a specific date.
     */
    @Operation(summary = "Get colleague shifts", description = "Retrieves shifts of colleagues on a specific date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of colleague shifts", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{shiftId}/delete")
    ResponseEntity<List<ShiftDTO>> getColleagueShifts(
            @Parameter(description = "Employee ID requesting colleague shifts", required = true) @PathVariable Long employeeId,
            @Parameter(description = "Date of shifts", required = true) @RequestParam LocalDate shiftDate,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Retrieves shifts of a specific employee managed by a manager.
     */
    @Operation(summary = "View employee shifts managed by manager", description = "Retrieves shifts of a specific employee managed by a manager.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of employee shifts", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Manager or employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/manager/{managerId}/employee/{employeeId}")
    ResponseEntity<List<ShiftDTO>> viewManagerEmployeeShifts(
            @Parameter(description = "Manager ID", required = true) @PathVariable Long managerId,
            @Parameter(description = "Employee ID", required = true) @PathVariable Long employeeId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);

    /**
     * Retrieves all shift swap requests for a manager to approve/reject.
     */
    @Operation(summary = "Get manager swap requests", description = "Retrieves all shift swap requests for a manager to approve/reject.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of shift swap requests", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ShiftDTO.class))),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Manager not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/manager/{managerId}/swap-requests")
    ResponseEntity<List<ShiftDTO>> getManagerSwapRequests(
            @Parameter(description = "Manager ID", required = true) @PathVariable Long managerId,
            @Parameter(description = "Employee Role", required = true) @RequestHeader("role") EmployeeRole role);
    
    @GetMapping("/employee/{employeeId}/upcoming")
    ResponseEntity<List<ShiftDTO>> getUpcomingEmployeeShifts(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role);
    
    
    
    @GetMapping("/employee/{employeeId}/swap/requests")
    ResponseEntity<List<ShiftDTO>> viewEmployeeSwapRequests(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role);

    @GetMapping("/employee/{employeeId}/swap/rejected")
    ResponseEntity<List<ShiftDTO>> viewEmployeeRejectedSwapRequests(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role);

    @GetMapping("/employee/{employeeId}/swap/approved")
    ResponseEntity<List<ShiftDTO>> viewEmployeeApprovedSwapRequests(@PathVariable Long employeeId,
    		@RequestHeader("role") EmployeeRole role);
    
    
    
}
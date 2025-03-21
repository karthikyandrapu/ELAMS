package com.elams.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elams.dto.LeaveBalanceDTO;
import com.elams.dto.LeaveModuleDTO;
import com.elams.service.LeaveBalanceServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for managing leave balance operations.
 * This controller provides endpoints for retrieving, updating, creating, and deleting leave balance records.
 */
@RestController
@RequestMapping("api/leave-balance")
@EnableAspectJAutoProxy
@Tag(name = "Leave Balance", description = "Operations related to leave balance")
public class LeaveBalanceController {

    private final LeaveBalanceServiceInterface leaveBalanceService;

    @Autowired
    public LeaveBalanceController(LeaveBalanceServiceInterface leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    /**
     * Retrieves the leave balance for a specific employee and leave type.
     *
     * @param leaveModuleDTO The DTO containing employee ID and leave type.
     * @return The leave balance DTO.
     */
    @Operation(summary = "Get leave balance", description = "Retrieves the leave balance for a specific employee and leave type.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved leave balance", content = @Content(schema = @Schema(implementation = LeaveBalanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @GetMapping("/getBalance")
    public LeaveBalanceDTO getLeaveBalance(@RequestBody LeaveModuleDTO leaveModuleDTO) {
        return leaveBalanceService.getLeaveBalance(leaveModuleDTO.getEmployeeId(), leaveModuleDTO.getLeaveType());
    }

    /**
     * Updates the leave balance for an employee.
     *
     * @param leaveModuleDTO The DTO containing employee ID, leave type, and requested days.
     * @return ResponseEntity containing the updated leave balance DTO.
     */
    @Operation(summary = "Update leave balance", description = "Updates the leave balance for an employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated leave balance", content = @Content(schema = @Schema(implementation = LeaveBalanceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Leave balance not found")
    })
    @PutMapping("/update")
    public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@Valid @RequestBody LeaveModuleDTO leaveModuleDTO) {
        LeaveBalanceDTO dto = leaveBalanceService.updateLeaveBalance(leaveModuleDTO.getEmployeeId(), leaveModuleDTO.getLeaveType(), leaveModuleDTO.getRequestedDays());
        return ResponseEntity.ok(dto);
    }

    /**
     * Creates a new leave balance record.
     *
     * @param leaveModuleDTO The DTO containing employee ID, leave type, and initial balance.
     * @return ResponseEntity containing the created leave balance DTO.
     */
    @Operation(summary = "Create leave balance", description = "Creates a new leave balance record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created leave balance", content = @Content(schema = @Schema(implementation = LeaveBalanceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/create")
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@Valid @RequestBody LeaveModuleDTO leaveModuleDTO) {
        LeaveBalanceDTO dto = leaveBalanceService.createLeaveBalance(leaveModuleDTO.getEmployeeId(), leaveModuleDTO.getLeaveType(), leaveModuleDTO.getBalance());
        return ResponseEntity.ok(dto);
    }

    /**
     * Retrieves all leave balances for a specific employee.
     *
     * @param leaveModuleDTO The DTO containing employee ID.
     * @return ResponseEntity containing a list of leave balance DTOs.
     */
    @Operation(summary = "Get all leave balances", description = "Retrieves all leave balances for a specific employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all leave balances", content = @Content(schema = @Schema(implementation = LeaveBalanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Leave balances not found")
    })
    @GetMapping("/all")
    public ResponseEntity<List<LeaveBalanceDTO>> getAllLeaveBalancesForEmployees(@RequestBody LeaveModuleDTO leaveModuleDTO) {
        List<LeaveBalanceDTO> dtoList = leaveBalanceService.getAllLeaveBalanceOfEmployee(leaveModuleDTO.getEmployeeId());
        return ResponseEntity.ok(dtoList);
    }

    /**
     * Checks if the employee has sufficient leave balance for the requested leave.
     *
     * @param leaveModuleDTO The DTO containing employee ID, leave type, and requested days.
     * @return A string indicating whether the balance is sufficient.
     */
    @Operation(summary = "Check sufficient balance", description = "Checks if the employee has sufficient leave balance for the requested leave.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked balance", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @GetMapping("/sufficient")
    public String checkIfBalanceIsSufficient(@Valid @RequestBody LeaveModuleDTO leaveModuleDTO) {
        boolean status = leaveBalanceService.hasSufficientBalance(leaveModuleDTO.getEmployeeId(), leaveModuleDTO.getLeaveType(), leaveModuleDTO.getRequestedDays());
        if (status) {
            return "Balance is sufficient";
        } else {
            return "Your balance is not sufficient for the leave";
        }
    }

    /**
     * Deletes all leave balances for a specific employee.
     *
     * @param leaveModuleDTO The DTO containing employee ID.
     * @return ResponseEntity containing a success message.
     */
    @Operation(summary = "Delete all leave balances", description = "Deletes all leave balances for a specific employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted all leave balances", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Leave balances not found")
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> deleteAllLeaveBalancesForEmployee(@RequestBody LeaveModuleDTO leaveModuleDTO) {
        leaveBalanceService.deleteAllLeaveBalancesForEmployee(leaveModuleDTO.getEmployeeId());
        return ResponseEntity.ok("All leave balances for Employee ID " + leaveModuleDTO.getEmployeeId() + " have been deleted.");
    }
}
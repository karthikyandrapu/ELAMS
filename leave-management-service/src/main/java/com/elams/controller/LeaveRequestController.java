package com.elams.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elams.dto.EmployeeDetailsDTO;
 
import com.elams.entities.LeaveRequest;
import com.elams.exception.LeaveRequestResourceNotFoundException;
import com.elams.service.LeaveRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

//port=8052
/**
 * Handles leave request operations.
 *
 * @apiNote This controller provides APIs for requesting, reviewing, and retrieving leave requests.
 */
@RestController
@RequestMapping("/leave-requests")
public class LeaveRequestController {
	
 
	private final LeaveRequestService leaveRequestService;
	public LeaveRequestController(LeaveRequestService leaveRequestService) {
		this.leaveRequestService = leaveRequestService;
	}

    /**
     * Request leave for an employee.
     *
     * @apiNote This method allows an employee to submit a leave request.
     * @implSpec Returns the created leave request details.
     * @param employeeDetails - details of the employee submitting the request.
     * @return ResponseEntity<LeaveRequest> - the created leave request details.
     * @see LeaveRequest
     */
    @Operation(summary = "Request Leave", description = "Submit a leave request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully submitted the leave request."),
        @ApiResponse(responseCode = "400", description = "Invalid input.")
    })
    @PostMapping("/request")
    public ResponseEntity<LeaveRequest> requestLeave(@Validated @RequestBody EmployeeDetailsDTO employeeDetails) {
        return leaveRequestService.requestLeave(employeeDetails.getId(), employeeDetails.getManagerId(),
                employeeDetails.getLeaveType(), employeeDetails.getStartDate(), employeeDetails.getEndDate());
    }

    /**
     * Review a leave request.
     *
     * @apiNote This method allows a manager to review a leave request.
     * @implSpec Returns the updated leave request details after review.
     * @param leaveId - ID of the leave request.
     * @param decision - Manager's decision (approved/rejected).
     * @param managerId - ID of the reviewing manager.
     * @return ResponseEntity<LeaveRequest> - the updated leave request details.
     * @throws LeaveRequestResourceNotFoundException if the leave request or manager is not found.
     */
    @Operation(summary = "Review Leave Request", description = "Review a leave request by the manager.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully reviewed the leave request."),
        @ApiResponse(responseCode = "404", description = "Leave request or manager not found.")
    })
    @PutMapping("/{leaveId}/review/{managerId}")
    public ResponseEntity<LeaveRequest> reviewLeaveRequest(@PathVariable Long leaveId,
                                                            @RequestParam String decision,
                                                            @PathVariable Long managerId) throws LeaveRequestResourceNotFoundException {
        return leaveRequestService.reviewLeaveRequest(leaveId, decision, managerId);
    }

    /**
     * Get the status of a leave request.
     *
     * @apiNote This method fetches the status of a specific leave request for an employee.
     * @implSpec Returns the leave request status details.
     * @param leaveId - ID of the leave request.
     * @param employeeId - ID of the employee.
     * @return ResponseEntity<LeaveRequest> - the leave request status details.
     * @throws LeaveRequestResourceNotFoundException if the leave request or employee is not found.
     */
    @Operation(summary = "Get Leave Request Status", description = "Retrieve the status of a specific leave request.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the leave request status."),
        @ApiResponse(responseCode = "404", description = "Leave request or employee not found.")
    })
    @GetMapping("/{leaveId}/status/{employeeId}")
    public ResponseEntity<LeaveRequest> getLeaveRequestStatus(@PathVariable Long leaveId,
                                                               @PathVariable Long employeeId) throws LeaveRequestResourceNotFoundException {
        return leaveRequestService.leaveRequestStatusForEmployee(leaveId, employeeId);
    }

    /**
     * Get all leave requests.
     *
     * @apiNote This method retrieves all leave requests.
     * @implSpec Returns an empty list if no leave requests are found.
     * @return ResponseEntity<List<LeaveRequest>> - a list of leave requests.
     */
    @Operation(summary = "Get All Leave Requests", description = "Retrieve all leave requests.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all leave requests.")
    })
    @GetMapping("/all")
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return leaveRequestService.getAllLeaveRequests();
    }

    /**
     * Get leave requests by employee ID.
     *
     * @apiNote This method retrieves all leave requests for a specific employee.
     * @param employeeId - ID of the employee.
     * @return ResponseEntity<List<LeaveRequest>> - a list of leave requests for the employee.
     */
    @Operation(summary = "Get Leave Requests by Employee ID", description = "Retrieve all leave requests for a specific employee.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the leave requests for the employee.")
    })
    @GetMapping("/employee")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployeeId(@RequestParam Long employeeId) {
        return leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
    }

    /**
     * Get leave requests by manager ID.
     *
     * @apiNote This method retrieves all leave requests assigned to a specific manager.
     * @param managerId - ID of the manager.
     * @return ResponseEntity<List<LeaveRequest>> - a list of leave requests assigned to the manager.
     */
    @Operation(summary = "Get Leave Requests by Manager ID", description = "Retrieve all leave requests assigned to a specific manager.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the leave requests for the manager.")
    })
    @GetMapping("/manager")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByManagerId(@RequestParam Long managerId) {
        return leaveRequestService.getLeaveRequestsByManagerId(managerId);
    }
} 
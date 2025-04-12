package com.elams.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.elams.dto.EmployeesDTO;
import com.elams.dto.LeaveBalanceCheckDTO;
import com.elams.dto.LeaveBalanceUpdateDTO;
import com.elams.entities.LeaveRequest;
import com.elams.entities.LeaveStatus;
import com.elams.entities.LeaveType;
import com.elams.exception.LeaveRequestResourceNotFoundException;
import com.elams.feign.EmployeeServiceClient;
import com.elams.feign.LeaveBalanceClient;
import com.elams.repositories.LeaveRequestRepository;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final EmployeeServiceClient employeeClient;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveBalanceClient leaveBalanceClient;
    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestServiceImpl.class);

    public LeaveRequestServiceImpl(EmployeeServiceClient employeeClient, LeaveRequestRepository leaveRequestRepository,
                                   LeaveBalanceClient leaveBalanceClient) {
        this.employeeClient = employeeClient;
        this.leaveRequestRepository = leaveRequestRepository;
        this.leaveBalanceClient = leaveBalanceClient;
    }

    @Override
    public ResponseEntity<LeaveRequest> requestLeave(Long id, Long managerId, LeaveType leaveType, LocalDate startDate, LocalDate endDate) {
        LeaveRequest request = new LeaveRequest();
        request.setEmployeeId(id);
        request.setStatus(LeaveStatus.PENDING);
        request.setLeaveType(leaveType);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        EmployeesDTO employee = employeeClient.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }

        EmployeesDTO manager = employeeClient.getEmployeeById(employee.getManagerId());
        if (manager == null) {
            return ResponseEntity.notFound().build();
        }
        
        System.out.println("After if");
        logger.info("Employee ID: {}, Manager ID from Employee Service: {}", id, employee.getManagerId());
        List<EmployeesDTO> subordinates = employeeClient.getEmployeesByManager(managerId);
        boolean isSubordinate = false;
        for (EmployeesDTO sub : subordinates) {
            if (sub.getId().equals(id)) {
                isSubordinate = true;
                break;
            }
        }
        if (!isSubordinate) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        System.out.println("After subordinate");

//        List<LeaveRequest> existingRequests = leaveRequestRepository.findByEmployeeIdAndStatus(id, LeaveStatus.PENDING);
//        if (!existingRequests.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }

        leaveRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @Override
    public ResponseEntity reviewLeaveRequest(Long leaveId, String decision, Long managerId)
            throws LeaveRequestResourceNotFoundException {
        logger.info("Reviewing leave request with ID: {} by manager ID: {} and decision: {}", leaveId, managerId, decision);

        // Fetch the leave request
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> {
                    logger.error("Leave request not found with ID: {}", leaveId);
                    return new LeaveRequestResourceNotFoundException("Leave request not found with id: " + leaveId);
                });
        
        System.out.println(leaveRequest.getStartDate());
        System.out.println(leaveRequest.getEndDate());
        // Fetch the employee details
        EmployeesDTO employee = employeeClient.getEmployeeById(leaveRequest.getEmployeeId());
        if (employee == null || !employee.getManagerId().equals(managerId)) {
            logger.warn("Manager ID: {} is not authorized to review leave request for employee ID: {}", managerId, leaveRequest.getEmployeeId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        // Decision handling
        if (decision.equalsIgnoreCase("APPROVED")) {
            logger.info("Leave request ID: {} is being reviewed for approval", leaveId);

            // Prepare leave balance check
            LeaveBalanceCheckDTO leaveBalanceCheckDTO = new LeaveBalanceCheckDTO(
                    leaveRequest.getEmployeeId(),
                    leaveRequest.getLeaveType(),
                    (double) ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate())
            );
            
            System.out.println(leaveBalanceCheckDTO.getEmployeeId());
            System.out.println(leaveBalanceCheckDTO.getLeaveType());
            System.out.println(leaveBalanceCheckDTO.getDays());
            System.out.print("no baalance");
            
          


            String leaveBalanceResponse = leaveBalanceClient.hasSufficientBalance(leaveBalanceCheckDTO);
            logger.info("Leave balance check response for employee ID: {} - {}", leaveRequest.getEmployeeId(), leaveBalanceResponse);

            // Handle insufficient balance
            if (leaveBalanceResponse == null || leaveBalanceResponse.equalsIgnoreCase("false")) {
                logger.error("Insufficient leave balance for employee ID: {}", leaveRequest.getEmployeeId());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No sufficient leave balance.");
            }

            // Approve the leave request and update balance
            leaveRequest.setStatus(LeaveStatus.APPROVED);
            logger.info("Leave request ID: {} approved, updating leave balance for employee ID: {}", leaveId, leaveRequest.getEmployeeId());

            LeaveBalanceUpdateDTO leaveBalanceUpdateDTO = new LeaveBalanceUpdateDTO(
                    leaveRequest.getEmployeeId(),
                    leaveRequest.getLeaveType(),
                    (double) ChronoUnit.DAYS.between(leaveRequest.getStartDate(),leaveRequest.getEndDate())
            );
            logger.info("Daysssssssssssss {}",leaveBalanceUpdateDTO.getDays());
            leaveBalanceClient.updateLeaveBalance(leaveBalanceUpdateDTO);
            logger.info("Leave balance updated successfully for employee ID: {}", leaveRequest.getEmployeeId());
        } else if (decision.equalsIgnoreCase("REJECTED")) {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
            logger.info("Leave request ID: {} rejected by manager ID: {}", leaveId, managerId);
        } else {
            logger.error("Invalid decision provided for leave request ID: {}", leaveId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Save the updated leave request
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request ID: {} updated successfully with status: {}", leaveId, leaveRequest.getStatus());
        return ResponseEntity.ok(updatedLeaveRequest);
    }

    @Override
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        return ResponseEntity.ok(leaveRequests);
    }

    @Override
    public ResponseEntity getLeaveRequestsByEmployeeId(Long id) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeId(id);
        if (leaveRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Employee ID is invalid or has no leave requests.");
        }
        return ResponseEntity.ok(leaveRequests);
    }


    @Override
    public ResponseEntity getLeaveRequestsByManagerId(Long managerId) {
        // Fetch subordinates managed by the given managerId
        List<EmployeesDTO> subordinates = employeeClient.getEmployeesByManager(managerId);

        // Check if subordinates are null or empty
        if (subordinates == null || subordinates.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No employees found under the given manager ID: " + managerId);
        }

        // Map employee IDs from the subordinates list
        List<Long> employeeIds = subordinates.stream().map(EmployeesDTO::getId).toList();

        // Fetch leave requests for the employees
        List<LeaveRequest> leaves = leaveRequestRepository.findByEmployeeIdIn(employeeIds);

        return ResponseEntity.ok(leaves);
    }


    @Override
    public ResponseEntity<LeaveRequest> leaveRequestStatusForEmployee(Long leaveId, Long employeeId)
            throws LeaveRequestResourceNotFoundException {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveRequestResourceNotFoundException("Leave request not found with id: " + leaveId));
        if (!leaveRequest.getEmployeeId().equals(employeeId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        return ResponseEntity.ok(leaveRequest);
    }
}
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

        List<LeaveRequest> existingRequests = leaveRequestRepository.findByEmployeeIdAndStatus(id, LeaveStatus.PENDING);
        if (existingRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        leaveRequestRepository.save(request);
        return ResponseEntity.ok(request);
    }

    @Override
    public ResponseEntity<LeaveRequest> reviewLeaveRequest(Long leaveId, String decision, Long managerId)
            throws LeaveRequestResourceNotFoundException {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new LeaveRequestResourceNotFoundException("Leave request not found with id: " + leaveId));
        EmployeesDTO employee = employeeClient.getEmployeeById(leaveRequest.getEmployeeId());
        LocalDate start = leaveRequest.getStartDate();
        LocalDate end = leaveRequest.getEndDate();
        long daysBetween = ChronoUnit.DAYS.between(start, end);

        if (employee == null || !employee.getManagerId().equals(managerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (decision.equalsIgnoreCase("APPROVED")) {
            logger.info("Checking leave balance sufficiency for employeeId: {}, leaveType: {}, days: {}",
                    leaveRequest.getEmployeeId(), leaveRequest.getLeaveType(), (double) daysBetween);
            LeaveBalanceCheckDTO leaveBalanceCheckDTO = new LeaveBalanceCheckDTO(leaveRequest.getEmployeeId(), leaveRequest.getLeaveType(), (double) daysBetween);
            String leaveBalanceResponse = leaveBalanceClient.hasSufficientBalance(leaveBalanceCheckDTO);

            logger.info("Leave balance sufficiency response: {}", leaveBalanceResponse);

            if (leaveBalanceResponse == null || !leaveBalanceResponse.equalsIgnoreCase("true")) {
                logger.warn("Insufficient leave balance or leave balance service returned null/false.");
                return ResponseEntity.badRequest().build();
            }

            leaveRequest.setStatus(LeaveStatus.APPROVED);
            logger.info("Updating leave balance for employeeId: {}, leaveType: {}, days taken: {}",
                    leaveRequest.getEmployeeId(), leaveRequest.getLeaveType(), (double) daysBetween);
            LeaveBalanceUpdateDTO leaveBalanceUpdateDTO = new LeaveBalanceUpdateDTO(leaveRequest.getEmployeeId(), leaveRequest.getLeaveType(), (double) daysBetween);
            leaveBalanceClient.updateLeaveBalance(leaveBalanceUpdateDTO);
            logger.info("Leave balance updated successfully.");
        } else if (decision.equalsIgnoreCase("REJECTED")) {
            leaveRequest.setStatus(LeaveStatus.REJECTED);
        } else {
            return ResponseEntity.badRequest().build();
        }

        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return ResponseEntity.ok(updatedLeaveRequest);
    }

    @Override
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();
        return ResponseEntity.ok(leaveRequests);
    }

    @Override
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployeeId(Long id) {
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeId(id);
        return ResponseEntity.ok(leaveRequests);
    }

    @Override
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByManagerId(Long managerId) {
        List<EmployeesDTO> subordinates = employeeClient.getEmployeesByManager(managerId);
        if (subordinates == null || subordinates.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        List<Long> employeeIds = subordinates.stream().map(EmployeesDTO::getId).toList();
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
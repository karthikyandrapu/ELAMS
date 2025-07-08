package com.elams.service;

import com.elams.dto.EmployeesDTO;
import com.elams.dto.LeaveBalanceCheckDTO;
import com.elams.dto.LeaveBalanceUpdateDTO;
import com.elams.entities.LeaveRequest;
import com.elams.entities.LeaveStatus;
import com.elams.entities.LeaveType;
import com.elams.exception.LeaveRequestResourceNotFoundException;
import com.elams.feign.EmployeeServiceClient;
import com.elams.feign.LeaveBalanceClient;
import com.elams.modelmapper.LeaveRequestMapper;
import com.elams.repositories.LeaveRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class LeaveRequestServiceImplTest {

    @Mock
    private EmployeeServiceClient employeeClient;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceClient leaveBalanceClient;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    private EmployeesDTO employee;
    private EmployeesDTO manager; //these are typically initialized in the @BeforeEach method
    private LeaveRequest leaveRequestobj;

    @BeforeEach
    void setUp() {
        employee = new EmployeesDTO();
        employee.setId(1L);
        employee.setManagerId(2L);

        manager = new EmployeesDTO();
        manager.setId(2L);

        leaveRequestobj = new LeaveRequest();
        leaveRequestobj.setLeaveId(10L);
        leaveRequestobj.setEmployeeId(1L);
        leaveRequestobj.setLeaveType(LeaveType.CASUAL);
        leaveRequestobj.setStartDate(LocalDate.of(2024, 1, 1));
        leaveRequestobj.setEndDate(LocalDate.of(2024, 1, 5));
        leaveRequestobj.setStatus(LeaveStatus.PENDING);
    }

    
    @Test
    void requestLeave_EmployeeNotFound() {
        when(employeeClient.getEmployeeById(1L)).thenReturn(null);
        ResponseEntity<LeaveRequest> response = leaveRequestService.requestLeave(1L, 2L, LeaveType.CASUAL, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void requestLeave_ManagerNotFound() {
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
        when(employeeClient.getEmployeeById(2L)).thenReturn(null);
        ResponseEntity<LeaveRequest> response = leaveRequestService.requestLeave(1L, 2L, LeaveType.CASUAL, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void requestLeave_NotSubordinate() {
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
        when(employeeClient.getEmployeeById(2L)).thenReturn(manager);
        when(employeeClient.getEmployeesByManager(2L)).thenReturn(Collections.emptyList());
        ResponseEntity<LeaveRequest> response = leaveRequestService.requestLeave(1L, 2L, LeaveType.CASUAL, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void reviewLeaveRequest_Approved_Success() throws LeaveRequestResourceNotFoundException {
        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequestobj));
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
        when(leaveBalanceClient.hasSufficientBalance(any(LeaveBalanceCheckDTO.class))).thenReturn("true");
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequestobj);

        ResponseEntity<LeaveRequest> response = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LeaveStatus.APPROVED, response.getBody().getStatus());
        verify(leaveBalanceClient, times(1)).updateLeaveBalance(any(LeaveBalanceUpdateDTO.class));
    }

    @Test
    void reviewLeaveRequest_Rejected_Success() throws LeaveRequestResourceNotFoundException {
        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequestobj));
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequestobj);

        ResponseEntity<LeaveRequest> response = leaveRequestService.reviewLeaveRequest(10L, "REJECTED", 2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LeaveStatus.REJECTED, response.getBody().getStatus());
    }

    @Test
    void reviewLeaveRequest_InvalidDecision() throws LeaveRequestResourceNotFoundException {
        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequestobj));
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);

        ResponseEntity<LeaveRequest> response = leaveRequestService.reviewLeaveRequest(10L, "INVALID", 2L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void LeaveRequestStatusForEmployee_Success() throws LeaveRequestResourceNotFoundException {
        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequestobj));
        ResponseEntity<LeaveRequest> response = leaveRequestService.leaveRequestStatusForEmployee(10L, 1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(leaveRequestobj, response.getBody());
    }

    @Test
    void getAllLeaveRequests_Success() {
        List<LeaveRequest> leaveRequests = Arrays.asList(leaveRequestobj);
        when(leaveRequestRepository.findAll()).thenReturn(leaveRequests);
        ResponseEntity<List<LeaveRequest>> response = leaveRequestService.getAllLeaveRequests();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(leaveRequests, response.getBody());
    }

    @Test
    void getLeaveRequestsByEmployeeId_Success() {
        List<LeaveRequest> leaveRequests = Arrays.asList(leaveRequestobj);
        when(leaveRequestRepository.findByEmployeeId(1L)).thenReturn(leaveRequests);
        ResponseEntity<List<LeaveRequest>> response = leaveRequestService.getLeaveRequestsByEmployeeId(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(leaveRequests, response.getBody());
    }

    @Test
    void getLeaveRequestsByManagerId_Success() {
        List<EmployeesDTO> subordinates = Arrays.asList(employee);
        List<LeaveRequest> leaveRequests = Arrays.asList(leaveRequestobj);
        when(employeeClient.getEmployeesByManager(2L)).thenReturn(subordinates);
        when(leaveRequestRepository.findByEmployeeIdIn(Collections.singletonList(1L))).thenReturn(leaveRequests);
        ResponseEntity<List<LeaveRequest>> response = leaveRequestService.getLeaveRequestsByManagerId(2L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(leaveRequests, response.getBody());
    }
    @Test
    void requestLeave_InvalidEmployeeId() {
        when(employeeClient.getEmployeeById(999L)).thenReturn(null);

        ResponseEntity<LeaveRequest> response = leaveRequestService.requestLeave(999L, 2L, LeaveType.CASUAL, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 5));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    
    @Test
    void leaveRequestStatusForEmployee_EmployeeIdMismatch_Forbidden() throws LeaveRequestResourceNotFoundException {
        LeaveRequest mismatchedLeaveRequest = new LeaveRequest();
        mismatchedLeaveRequest.setLeaveId(10L);
        mismatchedLeaveRequest.setEmployeeId(99L); 

        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(mismatchedLeaveRequest));
        ResponseEntity<LeaveRequest> response = leaveRequestService.leaveRequestStatusForEmployee(10L, 1L); 
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(leaveRequestRepository, times(1)).findById(10L);
    }
    @Test
    void getLeaveRequestsByManagerId_NoSubordinates_ReturnsNotFound() {
        // Case 1: When subordinates are null
        when(employeeClient.getEmployeesByManager(2L)).thenReturn(null); 
        ResponseEntity<Object> responseWithNull = leaveRequestService.getLeaveRequestsByManagerId(2L);
        assertEquals(HttpStatus.NOT_FOUND, responseWithNull.getStatusCode());
        assertEquals("No employees found under the given manager ID: 2", responseWithNull.getBody());

        // Case 2: When subordinates list is empty
        when(employeeClient.getEmployeesByManager(2L)).thenReturn(Collections.emptyList()); 
        ResponseEntity<Object> responseWithEmptyList = leaveRequestService.getLeaveRequestsByManagerId(2L);
        assertEquals(HttpStatus.NOT_FOUND, responseWithEmptyList.getStatusCode());
        assertEquals("No employees found under the given manager ID: 2", responseWithEmptyList.getBody());

        verify(employeeClient, times(2)).getEmployeesByManager(2L);
    }

    @Test
    void reviewLeaveRequest_InsufficientBalanceOrInvalidResponse_ReturnsBadRequest() throws LeaveRequestResourceNotFoundException {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(10L);
        leaveRequest.setEmployeeId(1L);
        leaveRequest.setStartDate(LocalDate.of(2024, 1, 1)); 
        leaveRequest.setEndDate(LocalDate.of(2024, 1, 5));   

        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequest));
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
        when(leaveBalanceClient.hasSufficientBalance(any(LeaveBalanceCheckDTO.class))).thenReturn(null);

        ResponseEntity<LeaveRequest> responseWithNull = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);
        assertEquals(HttpStatus.BAD_REQUEST, responseWithNull.getStatusCode());
        when(leaveBalanceClient.hasSufficientBalance(any(LeaveBalanceCheckDTO.class))).thenReturn("false");

        ResponseEntity<LeaveRequest> responseWithFalse = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);
        assertEquals(HttpStatus.BAD_REQUEST, responseWithFalse.getStatusCode());
        verify(leaveRequestRepository, times(2)).findById(10L);
        verify(employeeClient, times(2)).getEmployeeById(1L);
        verify(leaveBalanceClient, times(2)).hasSufficientBalance(any(LeaveBalanceCheckDTO.class));
    }
    @Test
    void reviewLeaveRequest_EmployeeNullOrManagerMismatch_Forbidden() throws LeaveRequestResourceNotFoundException {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(10L);
        leaveRequest.setEmployeeId(1L);
        leaveRequest.setStartDate(LocalDate.of(2024, 1, 1)); 
        leaveRequest.setEndDate(LocalDate.of(2024, 1, 5));  
        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequest));
        when(employeeClient.getEmployeeById(1L)).thenReturn(null);

        ResponseEntity<LeaveRequest> responseEmployeeNull = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);
        assertEquals(HttpStatus.FORBIDDEN, responseEmployeeNull.getStatusCode());
        EmployeesDTO mismatchedEmployee = new EmployeesDTO();
        mismatchedEmployee.setId(1L);
        mismatchedEmployee.setManagerId(99L); 

        when(employeeClient.getEmployeeById(1L)).thenReturn(mismatchedEmployee);

        ResponseEntity<LeaveRequest> responseManagerMismatch = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L); 
        assertEquals(HttpStatus.FORBIDDEN, responseManagerMismatch.getStatusCode());
        verify(leaveRequestRepository, times(2)).findById(10L);
        verify(employeeClient, times(2)).getEmployeeById(1L);
    }
    @Test
    void reviewLeaveRequest_EmployeeNullOrManagerMismatch_Forbidden1() throws LeaveRequestResourceNotFoundException {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(10L);
        leaveRequest.setEmployeeId(1L);
        leaveRequest.setStartDate(LocalDate.of(2024, 1, 1));
        leaveRequest.setEndDate(LocalDate.of(2024, 1, 5));
        leaveRequest.setLeaveType(LeaveType.CASUAL);
        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequest));
        when(employeeClient.getEmployeeById(1L)).thenReturn(null);
        ResponseEntity<LeaveRequest> responseNullEmployee = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);
        assertEquals(HttpStatus.FORBIDDEN, responseNullEmployee.getStatusCode());
        EmployeesDTO mismatchedEmployee = new EmployeesDTO();
        mismatchedEmployee.setId(1L);
        mismatchedEmployee.setManagerId(99L); 
        when(employeeClient.getEmployeeById(1L)).thenReturn(mismatchedEmployee);

        ResponseEntity<LeaveRequest> responseManagerMismatch = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);

        assertEquals(HttpStatus.FORBIDDEN, responseManagerMismatch.getStatusCode());
        verify(leaveRequestRepository, times(2)).findById(10L);
        verify(employeeClient, times(2)).getEmployeeById(1L);
    }
    @Test
    void reviewLeaveRequest_Approve_Success() throws LeaveRequestResourceNotFoundException {
  
        LeaveRequest leaveRequestToApprove = new LeaveRequest(); // Renamed to avoid shadowing
        leaveRequestToApprove.setLeaveId(10L);
        leaveRequestToApprove.setEmployeeId(1L);
        leaveRequestToApprove.setStartDate(LocalDate.of(2024, 1, 1));
        leaveRequestToApprove.setEndDate(LocalDate.of(2024, 1, 5));
        leaveRequestToApprove.setLeaveType(LeaveType.CASUAL);

        when(leaveRequestRepository.findById(10L)).thenReturn(Optional.of(leaveRequestToApprove));
        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
        when(leaveBalanceClient.hasSufficientBalance(any(LeaveBalanceCheckDTO.class))).thenReturn("true");
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequestToApprove);

     
        ResponseEntity<LeaveRequest> response = leaveRequestService.reviewLeaveRequest(10L, "APPROVED", 2L);

      
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(LeaveStatus.APPROVED, response.getBody().getStatus());
    }
  

   

}
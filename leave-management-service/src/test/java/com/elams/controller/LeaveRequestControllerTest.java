package com.elams.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.elams.dto.EmployeeDetailsDTO;
import com.elams.entities.LeaveRequest;
import com.elams.entities.LeaveStatus;
import com.elams.entities.LeaveType;
import com.elams.service.LeaveRequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@ExtendWith(MockitoExtension.class)
class LeaveRequestControllerTest {

    @Mock
    private LeaveRequestService leaveRequestService;

    @InjectMocks
    private LeaveRequestController leaveRequestController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LeaveRequest leaveRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(leaveRequestController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule
        leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveId(1L);
        leaveRequest.setEmployeeId(200L);
        leaveRequest.setStartDate(LocalDate.of(2024, 12, 25));
        leaveRequest.setEndDate(LocalDate.of(2024, 12, 29));
        leaveRequest.setLeaveType(LeaveType.CASUAL);
        leaveRequest.setStatus(LeaveStatus.PENDING);
    }

    @Test
    void reviewLeaveRequest_success() throws Exception {
        when(leaveRequestService.reviewLeaveRequest(1L, "APPROVED", 100L)).thenReturn(ResponseEntity.ok(leaveRequest));

        mockMvc.perform(put("/leave-requests/1/review/100?decision=APPROVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaveId").value(1));
    }

    @Test
    void getLeaveRequestStatus_success() throws Exception {
        when(leaveRequestService.leaveRequestStatusForEmployee(1L, 200L)).thenReturn(ResponseEntity.ok(leaveRequest));

        mockMvc.perform(get("/leave-requests/1/status/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaveId").value(1));
    }

    @Test
    void getAllLeaveRequests_success() throws Exception {
        List<LeaveRequest> leaveRequests = Arrays.asList(leaveRequest);
        when(leaveRequestService.getAllLeaveRequests()).thenReturn(ResponseEntity.ok(leaveRequests));

        mockMvc.perform(get("/leave-requests/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leaveId").value(1));
    }

    @Test
    void getLeaveRequestsByEmployeeId_success() throws Exception {
        List<LeaveRequest> leaveRequests = Arrays.asList(leaveRequest);
        when(leaveRequestService.getLeaveRequestsByEmployeeId(200L)).thenReturn(ResponseEntity.ok(leaveRequests));

        mockMvc.perform(get("/leave-requests/employee?employeeId=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leaveId").value(1));
    }

    @Test
    void getLeaveRequestsByManagerId_success() throws Exception {
        List<LeaveRequest> leaveRequests = Arrays.asList(leaveRequest);
        when(leaveRequestService.getLeaveRequestsByManagerId(100L)).thenReturn(ResponseEntity.ok(leaveRequests));

        mockMvc.perform(get("/leave-requests/manager?managerId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].leaveId").value(1));
    }

    @Test
    void getLeaveRequestsByEmployeeId_noRequests() throws Exception {
        when(leaveRequestService.getLeaveRequestsByEmployeeId(200L))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/leave-requests/employee?employeeId=200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getLeaveRequestsByManagerId_noSubordinates() throws Exception {
        when(leaveRequestService.getLeaveRequestsByManagerId(100L))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mockMvc.perform(get("/leave-requests/manager?managerId=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void reviewLeaveRequest_noDecision() throws Exception {
        mockMvc.perform(put("/leave-requests/1/review/100"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getLeaveRequestStatus_notAuthorized() throws Exception {
        when(leaveRequestService.leaveRequestStatusForEmployee(1L, 999L))
                .thenReturn(ResponseEntity.status(HttpStatus.FORBIDDEN).build());

        mockMvc.perform(get("/leave-requests/1/status/999"))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestLeave_success() throws Exception {
        EmployeeDetailsDTO employeeDetails = new EmployeeDetailsDTO();
        employeeDetails.setId(200L);
        employeeDetails.setManagerId(100L);
        employeeDetails.setLeaveType(LeaveType.CASUAL);
        employeeDetails.setStartDate(LocalDate.of(2024, 12, 25));
        employeeDetails.setEndDate(LocalDate.of(2024, 12, 29));

        when(leaveRequestService.requestLeave(anyLong(), anyLong(), any(LeaveType.class), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(ResponseEntity.ok(leaveRequest));

        mockMvc.perform(post("/leave-requests/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.leaveId").value(1));
    }
}
package com.elams.controller;

import com.elams.dtos.AttendanceDTO;
import com.elams.dtos.ClockInOutDTO;
import com.elams.enums.EmployeeRole;
import com.elams.service.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(attendanceController).build();
        objectMapper = new ObjectMapper();
    }

    // Clock In Tests
    @Test
    void clockIn_validEmployeeId_returnsOk() throws Exception {
        ClockInOutDTO request = new ClockInOutDTO();
        request.setEmployeeId(1L);

        mockMvc.perform(post("/api/attendance/clockin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Clocked In"));

        verify(attendanceService, times(1)).clockIn(1L);
    }

    @Test
    void clockIn_invalidEmployeeId_returnsBadRequest() throws Exception {
        ClockInOutDTO request = new ClockInOutDTO();
        request.setEmployeeId(0L);

        mockMvc.perform(post("/api/attendance/clockin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee Id is Invalid"));

        verify(attendanceService, never()).clockIn(anyLong());
    }

    @Test
    void clockIn_nullEmployeeId_returnsBadRequest() throws Exception {
        ClockInOutDTO request = new ClockInOutDTO();
        request.setEmployeeId(null);

        mockMvc.perform(post("/api/attendance/clockin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee Id is Invalid"));

        verify(attendanceService, never()).clockIn(anyLong());
    }

    // Clock Out Tests
    @Test
    void clockOut_validEmployeeId_returnsOk() throws Exception {
        ClockInOutDTO request = new ClockInOutDTO();
        request.setEmployeeId(1L);

        mockMvc.perform(post("/api/attendance/clockout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully Clocked Out"));

        verify(attendanceService, times(1)).clockOut(1L);
    }

    @Test
    void clockOut_invalidEmployeeId_returnsBadRequest() throws Exception {
        ClockInOutDTO request = new ClockInOutDTO();
        request.setEmployeeId(0L);

        mockMvc.perform(post("/api/attendance/clockout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee Id is Invalid"));

        verify(attendanceService, never()).clockOut(anyLong());
    }

    @Test
    void clockOut_nullEmployeeId_returnsBadRequest() throws Exception {
        ClockInOutDTO request = new ClockInOutDTO();
        request.setEmployeeId(null);

        mockMvc.perform(post("/api/attendance/clockout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee Id is Invalid"));

        verify(attendanceService, never()).clockOut(anyLong());
    }

    // Get Attendance For Employee Tests
    @Test
    void getAttendanceForEmployee_validEmployeeId_returnsOk() throws Exception {
        Long employeeId = 1L;
        List<AttendanceDTO> attendanceDTOs = Arrays.asList(new AttendanceDTO(), new AttendanceDTO());
        when(attendanceService.getAttendanceForEmployee(employeeId)).thenReturn(ResponseEntity.ok(attendanceDTOs));

        mockMvc.perform(get("/api/attendance/employee/{employeeId}", employeeId)
                        .header("X-Employee-Role", EmployeeRole.MANAGER)
                        .header("X-Employee-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(attendanceService, times(1)).getAttendanceForEmployee(employeeId);
    }

    @Test
    void getAttendanceForEmployee_invalidRole_returnsForbidden() throws Exception {
        Long employeeId = 1L;

        mockMvc.perform(get("/api/attendance/employee/{employeeId}", employeeId)
                        .header("X-Employee-Role", EmployeeRole.EMPLOYEE)
                        .header("X-Employee-Id", 3L))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied. Only Managers Can Access..!!"));

        verify(attendanceService, never()).getAttendanceForEmployee(anyLong());
    }

    @Test
    void getAttendanceForEmployee_nullEmployeeId_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/attendance/employee/{employeeId}", (Object) null)
                        .header("X-Employee-Role", EmployeeRole.MANAGER)
                        .header("X-Employee-Id", 2L))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Employee ID cannot be null."));

        verify(attendanceService, never()).getAttendanceForEmployee(anyLong());
    }

    @Test
    void getAttendanceForEmployee_noRecordsFound_returnsNotFound() throws Exception{
        Long employeeId = 1L;
        when(attendanceService.getAttendanceForEmployee(employeeId)).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/api/attendance/employee/{employeeId}", employeeId)
                        .header("X-Employee-Role", EmployeeRole.MANAGER)
                        .header("X-Employee-Id", 2L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No attendance records found for employee ID: " + employeeId));
    }

    // Get Attendance For Today Tests
    @Test
    void getAttendanceForToday_validRole_returnsOk() throws Exception {
        List<AttendanceDTO> attendanceDTOs = Arrays.asList(new AttendanceDTO());
        when(attendanceService.getAttendanceForToday()).thenReturn(ResponseEntity.ok(attendanceDTOs));

        mockMvc.perform(get("/api/attendance/today")
                        .header("X-Employee-Role", EmployeeRole.MANAGER))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(attendanceService, times(1)).getAttendanceForToday();
    }

    @Test
    void getAttendanceForToday_invalidRole_returnsForbidden() throws Exception {
        mockMvc.perform(get("/api/attendance/today")
                        .header("X-Employee-Role", EmployeeRole.EMPLOYEE))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Access denied. Only managers can access today's attendance."));

        verify(attendanceService, never()).getAttendanceForToday();
    }

}
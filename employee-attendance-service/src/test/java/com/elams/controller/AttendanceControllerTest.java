package com.elams.controller;
import com.elams.AttendanceServiceApplication;
import com.elams.dtos.AttendanceDTO;
import com.elams.dtos.ClockInOutDTO;
import com.elams.enums.EmployeeRole;
import com.elams.exception.InvalidEmployeeIdException;
import com.elams.service.AttendanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes= {AttendanceServiceApplication.class})

class AttendanceControllerTest {

    @Mock
    private AttendanceService attendanceService;

    @InjectMocks
    private AttendanceController attendanceController;

    private ClockInOutDTO clockInOutDTO;
    private Long employeeId;
    private LocalDateTime now;
    private AttendanceDTO attendanceDTO;
    private EmployeeRole managerRole;
    private EmployeeRole employeeRole;

    @BeforeEach
    void setUp() {
        employeeId = 1L;
        now = LocalDateTime.now();
        clockInOutDTO = new ClockInOutDTO();
        clockInOutDTO.setEmployeeId(employeeId);
        attendanceDTO = new AttendanceDTO();
        attendanceDTO.setEmployeeId(employeeId);
        managerRole = EmployeeRole.MANAGER;
        employeeRole = EmployeeRole.EMPLOYEE;
    }

    @Test
    void clockIn_validRequest_returnsOk() {
        ResponseEntity<String> response = attendanceController.clockIn(clockInOutDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully Clocked In", response.getBody());
        verify(attendanceService, times(1)).clockIn(employeeId);
    }

    @Test
    void clockIn_invalidEmployeeId_returnsBadRequest() {
        clockInOutDTO.setEmployeeId(null);
        ResponseEntity<String> response = attendanceController.clockIn(clockInOutDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee Id is Invalid", response.getBody());
        clockInOutDTO.setEmployeeId(0L);
        response = attendanceController.clockIn(clockInOutDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee Id is Invalid", response.getBody());
    }

    @Test
    void clockOut_validRequest_returnsOk() {
        ResponseEntity<String> response = attendanceController.clockOut(clockInOutDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Successfully Clocked Out", response.getBody());
        verify(attendanceService, times(1)).clockOut(employeeId);
    }

    @Test
    void clockOut_invalidEmployeeId_returnsBadRequest() {
        clockInOutDTO.setEmployeeId(null);
        ResponseEntity<String> response = attendanceController.clockOut(clockInOutDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee Id is Invalid", response.getBody());
        clockInOutDTO.setEmployeeId(0L);
        response = attendanceController.clockOut(clockInOutDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee Id is Invalid", response.getBody());
    }

//    @Test
//    void getAttendanceForEmployee_validRequest_manager_returnsOk() {
//        when(attendanceService.getAttendanceForEmployee(employeeId)).thenReturn(ResponseEntity.ok(Arrays.asList(attendanceDTO)));
//        ResponseEntity<?> response = attendanceController.getAttendanceForEmployee(employeeId, managerRole, 2L);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        List<AttendanceDTO> actualBody = (List<AttendanceDTO>) response.getBody();
//        assertEquals(Arrays.asList(attendanceDTO), actualBody);
//
//    }

    
    @Test
    void getAttendanceForEmployee_nullEmployeeId_returnsBadRequest() {
        ResponseEntity<?> response = attendanceController.getAttendanceForEmployee(null, managerRole, 2L);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee ID cannot be null.", response.getBody());
    }

    @Test
    void getAttendanceForEmployee_noRecordsFound_returnsNotFound() {
        when(attendanceService.getAttendanceForEmployee(employeeId)).thenAnswer(invocation -> ResponseEntity.ok(List.of()));
        ResponseEntity<?> response = attendanceController.getAttendanceForEmployee(employeeId, managerRole, 2L);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No attendance records found for employee ID: " + employeeId, response.getBody());
    }



    @Test
    void getAttendanceForToday_invalidRequest_employee_returnsForbidden() {
        ResponseEntity<?> response = attendanceController.getAttendanceForToday(employeeRole);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied. Only managers can access today's attendance.", response.getBody());
    }

    @Test
    void getAttendanceForToday_noRecordsFound_returnsNotFound() {
        when(attendanceService.getAttendanceForToday()).thenAnswer(invocation -> ResponseEntity.ok(List.of()));
        ResponseEntity<?> response = attendanceController.getAttendanceForToday(managerRole);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("No attendance records found for today.", response.getBody());
    }


//    @Test
//    void getAttendanceForEmployeeAndDate_validRequest_manager_returnsOk() {
//        when(attendanceService.getAttendanceForEmployeeAndDate(employeeId, now)).thenAnswer(invocation -> ResponseEntity.ok(List.of()));
//        ResponseEntity<?> response = attendanceController.getAttendanceForEmployeeAndDate(employeeId, now, managerRole);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        // Extract the body (the list of AttendanceDTOs)
//        List<AttendanceDTO> actualBody = (List<AttendanceDTO>) response.getBody();
//
//        assertEquals(Arrays.asList(attendanceDTO), actualBody); // compare the list.
//    }

    @Test
    void getAttendanceForEmployeeAndDate_invalidRequest_employee_returnsForbidden() {
        ResponseEntity<?> response = attendanceController.getAttendanceForEmployeeAndDate(employeeId, now, employeeRole);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied. Only managers can access attendance by date.", response.getBody());
    }

    @Test
    void getAttendanceForEmployeeAndDate_nullEmployeeId_returnsBadRequest() {
        ResponseEntity<?> response = attendanceController.getAttendanceForEmployeeAndDate(null, now, managerRole);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee ID and Date cannot be null.", response.getBody());
    }

    @Test
    void getAttendanceForEmployeeAndDate_nullDate_returnsBadRequest() {
        ResponseEntity<?> response = attendanceController.getAttendanceForEmployeeAndDate(employeeId, null, managerRole);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Employee ID and Date cannot be null.", response.getBody());
    }


    @Test
    void countAttendance_validRequest_returnsOk() {
        when(attendanceService.countAttendance(employeeId, now.minusDays(1), now)).thenReturn(5);
        ResponseEntity<Integer> response = attendanceController.countAttendance(employeeId, now.minusDays(1), now);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody());
    }

    @Test
    void handleInvalidEmployeeIdException_returnsBadRequest() {
        InvalidEmployeeIdException exception = new InvalidEmployeeIdException("Invalid ID");
        ResponseEntity<String> response = attendanceController.handleInvalidEmployeeIdException(exception);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid ID", response.getBody());
    }
}
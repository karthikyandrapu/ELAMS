package com.elams.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.elams.dto.AttendanceReportDTO;
import com.elams.dto.AttendanceTrendsDTO;
import com.elams.dto.CalculateClockInCountDTO;
import com.elams.dto.updateAttendanceDTO;
import com.elams.entities.AttendanceReport;
import com.elams.entities.LeaveType;
import com.elams.enums.EmployeeRole;
import com.elams.exception.ResourceNotFoundException;
import com.elams.repository.AttendanceReportRepository;
import com.elams.repository.AttendanceTrendsRepository;
import com.elams.service.AttendanceReportService;
import com.elams.validation.UniqueAttendanceReportValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.discovery.EurekaClient;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;


@WebMvcTest(AttendanceReportController.class)
class AttendanceReportControllerTest {

	
	

    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    private Validator validator;
    
    @MockitoBean
    private EurekaClient discoveryClient;
    @MockitoBean
    private UniqueAttendanceReportValidator uniqueAttendanceReportValidator;
    @MockitoBean
    private AttendanceReportService attendanceReportService;
    @MockitoBean
    private AttendanceReportRepository attendanceReportRepository;
    @MockitoBean
    private AttendanceTrendsRepository attendanceTrendsRepository;
//    @InjectMocks
    private AttendanceReportController attendanceReportController;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @ParameterizedTest
    @CsvSource({
            "90.0, 10.0, Good Attendance",
            "0.0, 0.0, No data available",
            "70.0, 30.0, Average Attendance",
            "50.0, 50.0, Poor Attendance"
    })
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        attendanceReportController = new AttendanceReportController(attendanceReportService); // Initialize controller with mock
        assertNotNull(this);
    }
        
  


   @Test
    void testGetAllAttendanceReports() throws Exception {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(125L);
        report.setDateRange("2024-03-16 to 2024-03-31");
        report.setTotalAttendance(10);
        report.setAbsenteeism(2);

        List<AttendanceReport> reports = Arrays.asList(report);

        when(attendanceReportService.getAttendanceReports()).thenReturn(reports);

        mockMvc.perform(get("/api/attendance-reports"))
                .andExpect(status().isOk());

        verify(attendanceReportService, times(1)).getAttendanceReports();
    }


    @Test
    void testGetAttendanceReportById() throws Exception {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(125L);
        report.setDateRange("2024-03-16 to 2024-03-31");
        report.setTotalAttendance(10);
        report.setAbsenteeism(2);

        when(attendanceReportService.getAttendanceReportById(125L)).thenReturn(report);

        mockMvc.perform(get("/api/attendance-reports/125"))
                .andExpect(status().isOk());

        verify(attendanceReportService, times(1)).getAttendanceReportById(125L);
    }


    @Test
    void testCreateAttendanceReport() throws Exception {
        AttendanceReportDTO reportDTO = new AttendanceReportDTO();
        reportDTO.setEmployeeId(120L);
        reportDTO.setDateRange("2024-03-16 to 2024-03-31");
        reportDTO.setTotalAttendance(10);
        reportDTO.setAbsenteeism(2);

        Set<ConstraintViolation<AttendanceReportDTO>> violations = new HashSet<>(); // No violations

        when(validator.validate(any(AttendanceReportDTO.class))).thenReturn(violations);
        when(attendanceReportService.createAttendanceReport(any(AttendanceReport.class))).thenReturn(new AttendanceReport());

        mockMvc.perform(post("/api/attendance-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportDTO)))
                .andExpect(status().isCreated());

        verify(attendanceReportService, times(1)).createAttendanceReport(any(AttendanceReport.class));
    }

   
    @Test
    void testUpdateAttendanceReport() throws Exception {
        updateAttendanceDTO updateAttendance = new updateAttendanceDTO();
        updateAttendance.setEmployeeId(125L);
        updateAttendance.setTotalAttendance(12);
        updateAttendance.setAbsenteeism(1);

        when(attendanceReportService.updateAttendanceReport(any(Long.class), any(Integer.class), any(Integer.class))).thenReturn(true);

        mockMvc.perform(put("/api/attendance-reports/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateAttendance)))
                .andExpect(status().isOk());

        verify(attendanceReportService, times(1)).updateAttendanceReport(any(Long.class), any(Integer.class), any(Integer.class));
    }
    


    @Test
    void testDeleteAttendanceReport_success() throws Exception {
        when(attendanceReportService.deleteAttendanceReport(125L)).thenReturn(true);

        mockMvc.perform(delete("/api/attendance-reports/125"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Attendance Report Resource Removed"));

        verify(attendanceReportService, times(1)).deleteAttendanceReport(125L);
    }
    @Test
    void testDeleteAttendanceReport_notFound() throws Exception {
        when(attendanceReportService.deleteAttendanceReport(125L)).thenThrow(new ResourceNotFoundException("Attendance Report Resource not Found"));

        mockMvc.perform(delete("/api/attendance-reports/125"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Attendance Report Resource not Found"));

        verify(attendanceReportService, times(1)).deleteAttendanceReport(125L);
    }
   
    	
    
    @Test
    void testDeleteAllAttendanceReportsByEmployeeId_success() throws Exception {
        when(attendanceReportService.deleteAllAttendanceReportsByEmployeeId(125L)).thenReturn(true);

        mockMvc.perform(delete("/api/attendance-reports/all/125"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("All Attendance Reports for Employee 125 Removed"));

        verify(attendanceReportService, times(1)).deleteAllAttendanceReportsByEmployeeId(125L);
    }

    @Test
    void testCalculateAttendanceReport() throws Exception {
        CalculateClockInCountDTO calculateClockCount = new CalculateClockInCountDTO();
        calculateClockCount.setEmployeeId(125L);
        calculateClockCount.setDateRange("2024-03-16 to 2024-03-31");
        calculateClockCount.setLeaveType(LeaveType.VACATION);
        calculateClockCount.setRole(EmployeeRole.MANAGER);

        AttendanceReportDTO report = new AttendanceReportDTO();
        report.setEmployeeId(125L);
        report.setDateRange("2024-03-16 to 2024-03-31");
        report.setTotalAttendance(10);
        report.setAbsenteeism(2);

        when(attendanceReportService.calculateAttendanceReport(any(Long.class), any(String.class), any(LeaveType.class),any(EmployeeRole.class))).thenReturn(report);

        mockMvc.perform(post("/api/attendance-reports/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(calculateClockCount)))
                .andExpect(status().isOk());

        verify(attendanceReportService, times(1)).calculateAttendanceReport(any(Long.class), any(String.class), any(LeaveType.class),any(EmployeeRole.class));
    }

    @Test
    void testGetAllAttendanceReports_negative() throws Exception {
        when(attendanceReportService.getAttendanceReports())
                .thenThrow(new ResourceNotFoundException("Attendance Reports Not Found"));
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/attendance-reports").accept(MediaType.TEXT_PLAIN))
        .andExpect(status().isNotFound());


        
    }

    void testGetAttendanceTrends(double attendancePercentage, double absenteeismPercentage, String overallTrend) throws Exception {
        AttendanceTrendsDTO trendsDTO = new AttendanceTrendsDTO();
        trendsDTO.setEmployeeId(125L);
        trendsDTO.setDateRange("2024-01-01 to 2024-01-31");
        trendsDTO.setAverageAttendancePercentage(attendancePercentage);
        trendsDTO.setAverageAbsenteeismPercentage(absenteeismPercentage);
        trendsDTO.setOverallTrend(overallTrend);

        when(attendanceReportService.calculateAttendanceTrends(125L, "2024-01-01 to 2024-01-31")).thenReturn(trendsDTO);

        mockMvc.perform(get("/api/attendance-reports/trends/125/2024-01-01 to 2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(content().json(asJsonString(trendsDTO)));

        verify(attendanceReportService, times(1)).calculateAttendanceTrends(125L, "2024-01-01 to 2024-01-31");
    }
    
    @Test
    void testCreateAttendanceReport_validationFailure() throws Exception {
        AttendanceReportDTO reportDTO = new AttendanceReportDTO();
        // Intentionally leave some fields blank or invalid

        Set<ConstraintViolation<AttendanceReportDTO>> violations = new HashSet<>();
        // Add constraint violations to the set if needed to simulate specific failures

        when(validator.validate(any(AttendanceReportDTO.class))).thenReturn(violations);

        mockMvc.perform(post("/api/attendance-reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportDTO)))
                .andExpect(status().isBadRequest()); // Expect 400 for validation failure
    }

    

    @Test
    void testGetAttendanceReportsById() throws Exception {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(125L);
        report.setDateRange("2024-03-16 to 2024-03-31");
        report.setTotalAttendance(10);
        report.setAbsenteeism(2);

        List<AttendanceReport> reports = Arrays.asList(report);

        when(attendanceReportService.getAttendanceReportsById(125L)).thenReturn(reports);

        mockMvc.perform(get("/api/attendance-reports/reports/125"))
            .andExpect(status().isOk());

        verify(attendanceReportService, times(1)).getAttendanceReportsById(125L);
    }
    
    @Test
    void testDeleteAllAttendanceReportsByEmployeeId_notFound() throws Exception {
        when(attendanceReportService.deleteAllAttendanceReportsByEmployeeId(456L))
                .thenThrow(new ResourceNotFoundException("No attendance reports found for employee ID: 456"));

        mockMvc.perform(delete("/api/attendance-reports/all/456"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No attendance reports found for employee ID: 456"));

        verify(attendanceReportService, times(1)).deleteAllAttendanceReportsByEmployeeId(456L);
    }

    @Test
    void testDeleteAllAttendanceReportsByEmployeeId_failure() throws Exception {
        when(attendanceReportService.deleteAllAttendanceReportsByEmployeeId(789L)).thenReturn(false);

        mockMvc.perform(delete("/api/attendance-reports/all/789"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to remove all Attendance Reports for Employee 789"));

        verify(attendanceReportService, times(1)).deleteAllAttendanceReportsByEmployeeId(789L);
    }

    @Test
    void testCalculateAttendanceReport_validationFailure() throws Exception {
        CalculateClockInCountDTO calculateClockCount = new CalculateClockInCountDTO();
        

        Set<ConstraintViolation<CalculateClockInCountDTO>> violations = new HashSet<>();
        // Add constraint violations if needed
        

        when(validator.validate(any(CalculateClockInCountDTO.class))).thenReturn(violations);

        mockMvc.perform(post("/api/attendance-reports/calculate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(calculateClockCount)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAttendanceTrends_notFound() throws Exception {
        when(attendanceReportService.calculateAttendanceTrends(123L, "2024-03-01 to 2024-03-31"))
                .thenThrow(new ResourceNotFoundException("No attendance data found for employee 123 in date range 2024-03-01 to 2024-03-31"));

        mockMvc.perform(get("/api/attendance-reports/trends/123/2024-03-01 to 2024-03-31"))
                .andExpect(status().isNotFound());
        verify(attendanceReportService, times(1)).calculateAttendanceTrends(123L, "2024-03-01 to 2024-03-31");
    }

    @Test
    void testGetAttendanceReportsById_positive() throws Exception {
        AttendanceReport report1 = new AttendanceReport();
        report1.setEmployeeId(125L);
        report1.setDateRange("2024-01-01 to 2024-01-31");
        AttendanceReport report2 = new AttendanceReport();
        report2.setEmployeeId(125L);
        report2.setDateRange("2024-02-01 to 2024-02-29");

        List<AttendanceReport> reports = Arrays.asList(report1, report2);

        when(attendanceReportService.getAttendanceReportsById(125L)).thenReturn(reports);

        mockMvc.perform(get("/api/attendance-reports/reports/125"))
            .andExpect(status().isOk())
            .andExpect(content().json(asJsonString(reports)));

        verify(attendanceReportService, times(1)).getAttendanceReportsById(125L);
    }
    
    @Test
    void testDeleteEmployeeTrends_positive() {
        
        Long employeeId = 1L;
        doNothing().when(attendanceReportService).deleteTrendsByEmployeeId(employeeId);

        
        ResponseEntity<Void> response = attendanceReportController.deleteEmployeeTrends(employeeId);

       
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(attendanceReportService, times(1)).deleteTrendsByEmployeeId(employeeId);
    }

    @Test
    void testDeleteEmployeeTrends_negative_employeeIdNotFound() {
        
        Long employeeId = 999L;
        doThrow(new RuntimeException("Employee trends not found")).when(attendanceReportService).deleteTrendsByEmployeeId(employeeId);

        
        ResponseEntity<Void> response = attendanceReportController.deleteEmployeeTrends(employeeId);

        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(attendanceReportService, times(1)).deleteTrendsByEmployeeId(employeeId);
    }

    @Test
    void testGetEmployeesByAttendanceTrend_positive() {
        // Arrange
        Map<String, List<Long>> expectedTrends = new HashMap<>();
        expectedTrends.put("goodAttendance", Arrays.asList(1L, 2L));
        expectedTrends.put("averageAttendance", Arrays.asList(3L));
        expectedTrends.put("poorAttendance", Arrays.asList(4L, 5L));

        when(attendanceReportService.listEmployeesByAttendanceTrend()).thenReturn(expectedTrends);

        // Act
        ResponseEntity<Map<String, List<Long>>> response = attendanceReportController.getEmployeesByAttendanceTrend();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedTrends, response.getBody());
        verify(attendanceReportService, times(1)).listEmployeesByAttendanceTrend();
    }

    @Test
    void testGetEmployeesByAttendanceTrend_negative_noTrendsAvailable() {
        // Arrange
        when(attendanceReportService.listEmployeesByAttendanceTrend()).thenReturn(new HashMap<>());

        // Act
        ResponseEntity<Map<String, List<Long>>> response = attendanceReportController.getEmployeesByAttendanceTrend();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(attendanceReportService, times(1)).listEmployeesByAttendanceTrend();
    }

   

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
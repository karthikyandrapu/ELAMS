package com.elams.service;



import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import com.elams.ReportsAnalyticsServiceApplication;
import com.elams.dto.AttendanceReportDTO;
import com.elams.dto.AttendanceTrendsDTO;
import com.elams.entities.AttendanceReport;
import com.elams.entities.AttendanceTrends;
import com.elams.entities.LeaveType;
import com.elams.entities.Shift;
import com.elams.enums.EmployeeRole;
import com.elams.exception.ResourceNotFoundException;
import com.elams.repository.AttendanceReportRepository;
import com.elams.repository.AttendanceTrendsRepository;

import jakarta.validation.Validator;

@ContextConfiguration(classes = {ReportsAnalyticsServiceApplication.class})

class AttendanceReportServiceTest {

    @Mock
    private AttendanceReportRepository attendanceReportRepository;

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private AttendanceTrendsRepository attendanceTrendsRepository;
    
    @Mock
    private Validator validator;
   

    @InjectMocks
    private AttendanceReportServiceImpl attendanceReportServiceImpl;
    
    
    
    

    @BeforeEach
    void setUp()  {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown()  {
        attendanceReportRepository = null;
        attendanceReportServiceImpl = null;
        attendanceTrendsRepository = null;
    }


    @Test
    void test_getAllAttendanceReports_whenSizeIsOne_positive() {
        when(attendanceReportRepository.findAll()).thenAnswer(invocation -> {
            AttendanceReport report = new AttendanceReport();
            report.setEmployeeId(125L);
            report.setDateRange("2024-03-16 to 2024-03-31");
            report.setTotalAttendance(10);
            report.setAbsenteeism(2);
            return Arrays.asList(report);
        });

        List<AttendanceReport> actual = attendanceReportServiceImpl.getAttendanceReports();
        verify(attendanceReportRepository, times(1)).findAll();
        assertEquals(1,actual.size());
    }

    @Test
    void test_getAllAttendanceReports_RepositoryReturnsMoreThanEntity_positive() {
        when(attendanceReportRepository.findAll()).thenAnswer(invocation -> {
            AttendanceReport report1 = new AttendanceReport();
            report1.setEmployeeId(125L);
            report1.setDateRange("2024-03-16 to 2024-03-31");
            report1.setTotalAttendance(10);
            report1.setAbsenteeism(2);

            AttendanceReport report2 = new AttendanceReport();
            report2.setEmployeeId(126L);
            report2.setDateRange("2024-03-16 to 2024-03-31");
            report2.setTotalAttendance(12);
            report2.setAbsenteeism(1);
            return Arrays.asList(report1, report2);
        });

        List<AttendanceReport> actual = attendanceReportServiceImpl.getAttendanceReports();
        verify(attendanceReportRepository, times(1)).findAll();
        assertTrue(actual.size() > 1);
    }

    @Test
    void test_getAllAttendanceReports_RepositoryReturnsEmpty_negative() {
        when(attendanceReportRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            attendanceReportServiceImpl.getAttendanceReports();
        });
    }
    
    

    @Test
    void test_getAttendanceReportById_positive() {
        when(attendanceReportRepository.findByEmployeeId(125L)).thenAnswer(invocation -> {
            AttendanceReport report = new AttendanceReport();
            report.setEmployeeId(125L);
            report.setDateRange("2024-03-16 to 2024-03-31");
            report.setTotalAttendance(10);
            report.setAbsenteeism(2);
            return Arrays.asList(report);
        });

        AttendanceReport actual = attendanceReportServiceImpl.getAttendanceReportById(125L);
        assertEquals(10,actual.getTotalAttendance());
    }
    

    @Test
    void test_getAttendanceReportById_negative() {
        when(attendanceReportRepository.findByEmployeeId(any())).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> {
            attendanceReportServiceImpl.getAttendanceReportById(125L);
        });
    }

    @Test
    void test_saveAttendanceReport_positive() {
        AttendanceReport report = new AttendanceReport();
        report.setEmployeeId(125L);
        report.setDateRange("2024-03-16 to 2024-03-31");
        report.setTotalAttendance(10);
        report.setAbsenteeism(2);

        when(attendanceReportRepository.save(any())).thenReturn(report);

        AttendanceReport actual = attendanceReportServiceImpl.createAttendanceReport(report);
        assertEquals(report.getReportId(), actual.getReportId());
    }
    
    
    @Test
    void test_saveAttendanceReport_negative() {
        AttendanceReport report = null;

        try {
            attendanceReportServiceImpl.createAttendanceReport(report);
            assertTrue(false);
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
    

    @Test
    void test_updateAttendanceReport_positive() {
        when(attendanceReportRepository.findLatestByEmployeeId(125L)).thenAnswer(invocation -> {
            AttendanceReport report = new AttendanceReport();
            report.setEmployeeId(125L);
            report.setDateRange("2024-03-16 to 2024-03-31");
            report.setTotalAttendance(10);
            report.setAbsenteeism(2);
            return Optional.of(report);
        });

        boolean actual = attendanceReportServiceImpl.updateAttendanceReport(125L, 12, 1);
        verify(attendanceReportRepository, times(1)).save(any(AttendanceReport.class));
        assertTrue(actual);
    }
   

    @Test
    void test_updateAttendanceReport_negative() {
        when(attendanceReportRepository.findLatestByEmployeeId(any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            attendanceReportServiceImpl.updateAttendanceReport(125L, 12, 1);
        });
    }
   

    @Test
    void test_deleteAttendanceReport_positive() {
        when(attendanceReportRepository.findLatestByEmployeeId(125L)).thenReturn(Optional.of(new AttendanceReport()));

        assertDoesNotThrow(() -> {
            boolean result = attendanceReportServiceImpl.deleteAttendanceReport(125L);
            assertTrue(result);
        });

        verify(attendanceReportRepository, times(1)).deleteById(125L);
    }
    

    @Test
    void test_deleteAttendanceReport_negative() {
        when(attendanceReportRepository.findLatestByEmployeeId(125L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            attendanceReportServiceImpl.deleteAttendanceReport(125L);
        });

        verify(attendanceReportRepository, times(0)).deleteById(125L);
    }
   
    @Test
    void test_calculateAttendanceReport_positive() {
        Long employeeId = 125L;
        String dateRange = "2024-02-10 to 2024-03-20";
        LeaveType leaveType = LeaveType.VACATION;
        EmployeeRole role = EmployeeRole.EMPLOYEE;

        
        when(restTemplate.getForObject(contains("/count"), eq(Integer.class))).thenReturn(6);

        
        when(attendanceReportRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));


        Shift[] mockShifts = new Shift[]{}; 
        ResponseEntity<Shift[]> responseEntity = new ResponseEntity<>(mockShifts, HttpStatus.OK);
        when(restTemplate.exchange(
                contains("/employee/"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Shift[].class)
        )).thenReturn(responseEntity);

        
        when(restTemplate.getForObject(contains("/getbalance"), eq(Double.class))).thenReturn(10.0); // Example leave balance

        
        AttendanceReportDTO actual = attendanceReportServiceImpl.calculateAttendanceReport(employeeId, dateRange, leaveType,role);

        assertEquals(employeeId, actual.getEmployeeId());
        assertEquals(dateRange, actual.getDateRange());
        assertEquals(6, actual.getTotalAttendance());
        assertEquals(22, actual.getAbsenteeism());
        assertEquals(10.0, actual.getLeaveBalances().get(leaveType)); 
    }
    @Test
    void test_deleteAllAttendanceReportsByEmployeeId_positive() {
        Long employeeId = 125L;
        AttendanceReport report1 = new AttendanceReport();
        report1.setEmployeeId(employeeId);
        AttendanceReport report2 = new AttendanceReport();
        report2.setEmployeeId(employeeId);
        List<AttendanceReport> reports = Arrays.asList(report1, report2);

        when(attendanceReportRepository.findByEmployeeId(employeeId)).thenReturn(reports);
        assertDoesNotThrow(() -> assertTrue(attendanceReportServiceImpl.deleteAllAttendanceReportsByEmployeeId(employeeId)));
        verify(attendanceReportRepository, times(1)).deleteAll(reports);
    }
   
    @Test
    void test_deleteAllAttendanceReportsByEmployeeId_negative() {
        Long employeeId = 125L;
        when(attendanceReportRepository.findByEmployeeId(employeeId)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> attendanceReportServiceImpl.deleteAllAttendanceReportsByEmployeeId(employeeId));
        verify(attendanceReportRepository, times(0)).deleteAll(any());
    }
    

    @Test
    void test_calculateAttendanceTrends_positive() {
        Long employeeId = 125L;
        String dateRange = "2024-01-01 to 2024-01-31";
        AttendanceReport report1 = new AttendanceReport();
        report1.setEmployeeId(employeeId);
        report1.setDateRange(dateRange);
        report1.setTotalAttendance(18);
        report1.setAbsenteeism(2);
        AttendanceReport report2 = new AttendanceReport();
        report2.setEmployeeId(employeeId);
        report2.setDateRange(dateRange);
        report2.setTotalAttendance(16);
        report2.setAbsenteeism(4);
        List<AttendanceReport> reports = Arrays.asList(report1, report2);

        when(attendanceReportRepository.findByEmployeeId(employeeId)).thenReturn(reports);
        when(attendanceTrendsRepository.save(any(AttendanceTrends.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceTrendsDTO dto = attendanceReportServiceImpl.calculateAttendanceTrends(employeeId, dateRange);

        assertEquals(employeeId, dto.getEmployeeId());
        assertEquals(dateRange, dto.getDateRange());
        assertEquals(85.0, dto.getAverageAttendancePercentage());
        assertEquals(15.0, dto.getAverageAbsenteeismPercentage());
        assertEquals("Good Attendance", dto.getOverallTrend());
    }

    @Test
    void test_calculateAttendanceTrends_noData() {
        Long employeeId = 125L;
        String dateRange = "2024-01-01 to 2024-01-31";

        when(attendanceReportRepository.findByEmployeeId(employeeId)).thenReturn(Collections.emptyList());

        AttendanceTrendsDTO dto = attendanceReportServiceImpl.calculateAttendanceTrends(employeeId, dateRange);

        assertEquals("No data available", dto.getOverallTrend());
    }

    @Test
    void test_calculateAttendanceTrends_averageAttendance() {
        Long employeeId = 125L;
        String dateRange = "2024-01-01 to 2024-01-31";
        AttendanceReport report1 = new AttendanceReport();
        report1.setEmployeeId(employeeId);
        report1.setDateRange(dateRange);
        report1.setTotalAttendance(12);
        report1.setAbsenteeism(8);
        List<AttendanceReport> reports = Arrays.asList(report1);

        when(attendanceReportRepository.findByEmployeeId(employeeId)).thenReturn(reports);
        when(attendanceTrendsRepository.save(any(AttendanceTrends.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceTrendsDTO dto = attendanceReportServiceImpl.calculateAttendanceTrends(employeeId, dateRange);

        assertEquals("Average Attendance", dto.getOverallTrend());
    }

    
    @Test
    void test_calculateAttendanceTrends_poorAttendance() {
        Long employeeId = 125L;
        String dateRange = "2024-01-01 to 2024-01-31";
        AttendanceReport report1 = new AttendanceReport();
        report1.setEmployeeId(employeeId);
        report1.setDateRange(dateRange);
        report1.setTotalAttendance(10);
        report1.setAbsenteeism(10);
        List<AttendanceReport> reports = Arrays.asList(report1);

        when(attendanceReportRepository.findByEmployeeId(employeeId)).thenReturn(reports);
        when(attendanceTrendsRepository.save(any(AttendanceTrends.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceTrendsDTO dto = attendanceReportServiceImpl.calculateAttendanceTrends(employeeId, dateRange);

        assertEquals("Poor Attendance", dto.getOverallTrend());
        
        
    }
    
    @Test
    void testListEmployeesByAttendanceTrend_positive() {
        
        List<AttendanceTrends> trends = Arrays.asList(
                createTrend(1L, "Good Attendance"),
                createTrend(2L, "Average Attendance"),
                createTrend(3L, "Poor Attendance"),
                createTrend(4L, "Good Attendance"),
                createTrend(5L, "Average Attendance")
        );
        when(attendanceTrendsRepository.findAll()).thenReturn(trends);

        
        Map<String, List<Long>> result = attendanceReportServiceImpl.listEmployeesByAttendanceTrend();

        
        assertEquals(2, result.get("goodAttendance").size());
        assertEquals(2, result.get("averageAttendance").size());
        assertEquals(1, result.get("poorAttendance").size());

        assertTrue(result.get("goodAttendance").contains(1L));
        assertTrue(result.get("goodAttendance").contains(4L));
        assertTrue(result.get("averageAttendance").contains(2L));
        assertTrue(result.get("averageAttendance").contains(5L));
        assertTrue(result.get("poorAttendance").contains(3L));
    }

    @Test
    void testListEmployeesByAttendanceTrend_negative_emptyTrends() {
        
        when(attendanceTrendsRepository.findAll()).thenReturn(new ArrayList<>());

        
        Map<String, List<Long>> result = attendanceReportServiceImpl.listEmployeesByAttendanceTrend();

        
        assertTrue(result.get("goodAttendance").isEmpty());
        assertTrue(result.get("averageAttendance").isEmpty());
        assertTrue(result.get("poorAttendance").isEmpty());
    }

    @Test
    void testListEmployeesByAttendanceTrend_negative_nullTrends() {
        
        when(attendanceTrendsRepository.findAll()).thenReturn(null);

        
        Map<String, List<Long>> result = attendanceReportServiceImpl.listEmployeesByAttendanceTrend();

        
        assertTrue(result.get("goodAttendance").isEmpty());
        assertTrue(result.get("averageAttendance").isEmpty());
        assertTrue(result.get("poorAttendance").isEmpty());
    }

    @Test
    void testListEmployeesByAttendanceTrend_negative_invalidTrendString() {
       
        List<AttendanceTrends> trends = Arrays.asList(
                createTrend(1L, "Invalid Trend")
        );
        when(attendanceTrendsRepository.findAll()).thenReturn(trends);

        
        Map<String, List<Long>> result = attendanceReportServiceImpl.listEmployeesByAttendanceTrend();

        
        assertTrue(result.get("goodAttendance").isEmpty());
        assertTrue(result.get("averageAttendance").isEmpty());
        assertTrue(result.get("poorAttendance").isEmpty());
    }

    private AttendanceTrends createTrend(Long employeeId, String overallTrend) {
        AttendanceTrends trend = new AttendanceTrends();
        trend.setEmployeeId(employeeId);
        trend.setOverallTrend(overallTrend);
        return trend;
    }
    
   
}

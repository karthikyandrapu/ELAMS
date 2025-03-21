//package com.elams.service;
//
//import com.elams.AttendanceServiceApplication;
//import com.elams.dtos.AttendanceDTO;
//import com.elams.entities.Attendance;
//import com.elams.exception.InvalidEmployeeIdException;
//import com.elams.feign.EmployeeServiceClient;
//import com.elams.repository.AttendanceRepository;
//import com.elams.repository.EmployeeRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@ContextConfiguration(classes= {AttendanceServiceApplication.class})
//
//class AttendanceServiceImplTest {
//
//    @Mock
//    private AttendanceRepository attendanceRepository;
//
//    @Mock
//    private EmployeeServiceClient  employeeServiceClient;
//
//    @InjectMocks 
//    private AttendanceServiceImpl attendanceService;
//
//    private Attendance attendance1;
//    private Attendance attendance2;
//    private Long employeeId;
//    private LocalDateTime now;
//
//    @BeforeEach
//    void setUp() {
//        employeeId = 1L;
//        now = LocalDateTime.now();
//        attendance1 = new Attendance();
//        attendance1.setEmployeeId(employeeId);
//        attendance1.setClockInTime(now.minusHours(2));
//        attendance1.setClockOutTime(now.minusHours(1));
//        attendance1.setWorkHours(1.0);
//
//        attendance2 = new Attendance();
//        attendance2.setEmployeeId(employeeId);
//        attendance2.setClockInTime(now.minusDays(1));
//        attendance2.setClockOutTime(now.minusDays(1).plusHours(8));
//        attendance2.setWorkHours(8.0);
//    }
//
//    @Test
//    void clockIn_validEmployeeId_returnsAttendanceDTO() {
//        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance1);
//        AttendanceDTO dto = attendanceService.clockIn(employeeId);
//        assertNotNull(dto);
//        assertEquals(employeeId, dto.getEmployeeId());
//    }
//
//
//    @Test
//    void clockOut_validEmployeeId_returnsAttendanceDTO() {
//        when(attendanceRepository.findByEmployeeIdAndClockOutTimeIsNull(employeeId)).thenReturn(Optional.of(attendance1));
//        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance1);
//        AttendanceDTO dto = attendanceService.clockOut(employeeId);
//        assertNotNull(dto);
//        assertEquals(employeeId, dto.getEmployeeId());
//    }
//
//    @Test
//    void clockOut_invalidEmployeeId_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> attendanceService.clockOut(0L));
//        assertThrows(IllegalArgumentException.class, () -> attendanceService.clockOut(null));
//    }
//
//    @Test
//    void clockOut_clockInEntryNotFound_throwsRuntimeException() {
//        when(attendanceRepository.findByEmployeeIdAndClockOutTimeIsNull(employeeId)).thenReturn(Optional.empty());
//        assertThrows(RuntimeException.class, () -> attendanceService.clockOut(employeeId));
//    }
//
//    @Test
//    void getAttendanceForEmployee_validEmployeeId_returnsAttendanceDTOList() {
//        when(attendanceRepository.findByEmployeeId(employeeId)).thenReturn(Arrays.asList(attendance1, attendance2));
//        List<AttendanceDTO> dtoList = attendanceService.getAttendanceForEmployee(employeeId);
//        assertEquals(2, dtoList.size());
//        assertEquals(employeeId, dtoList.get(0).getEmployeeId());
//    }
//
//    @Test
//    void getAttendanceForEmployee_nullEmployeeId_throwsIllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> attendanceService.getAttendanceForEmployee(null));
//    }
//
//    @Test
//    void getAttendanceForToday_returnsAttendanceDTOList() {
//        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
//        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
//        when(attendanceRepository.findByClockInTimeBetween(startOfDay, endOfDay)).thenReturn(Arrays.asList(attendance1));
//        List<AttendanceDTO> dtoList = attendanceService.getAttendanceForToday();
//        assertEquals(1, dtoList.size());
//        assertEquals(employeeId, dtoList.get(0).getEmployeeId());
//    }
//
//    @Test
//    void getAttendanceForToday_noRecordsFound_returnsEmptyList() {
//        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
//        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
//        when(attendanceRepository.findByClockInTimeBetween(startOfDay, endOfDay)).thenReturn(Collections.emptyList());
//        List<AttendanceDTO> dtoList = attendanceService.getAttendanceForToday();
//        assertTrue(dtoList.isEmpty());
//    }
//
//    @Test
//    void getAttendanceForEmployeeAndDate_returnsAttendanceDTOList() {
//        LocalDateTime date = LocalDateTime.now();
//        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
//        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
//        when(attendanceRepository.findByEmployeeIdAndClockInTimeBetween(employeeId, startOfDay, endOfDay)).thenReturn(Arrays.asList(attendance1));
//        List<AttendanceDTO> dtoList = attendanceService.getAttendanceForEmployeeAndDate(employeeId, date);
//        assertEquals(1, dtoList.size());
//        assertEquals(employeeId, dtoList.get(0).getEmployeeId());
//    }
//
//    @Test
//    void countAttendance_returnsCount() {
//        LocalDateTime startDate = now.minusDays(2);
//        LocalDateTime endDate = now;
//        when(attendanceRepository.countByEmployeeIdAndClockInTimeBetween(employeeId, startDate, endDate)).thenReturn(2);
//        int count = attendanceService.countAttendance(employeeId, startDate, endDate);
//        assertEquals(2, count);
//    }
//
//
//
//    @Test
//    void clockOut_null_employeeId_throws_IllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> attendanceService.clockOut(null));
//    }
//
//    @Test
//    void getAttendanceForEmployee_null_employeeId_throws_IllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> attendanceService.getAttendanceForEmployee(null));
//    }
//
//
//
//    @Test
//    void clockIn_zero_employeeId_throws_InvalidEmployeeIdException() {
//        assertThrows(InvalidEmployeeIdException.class, () -> attendanceService.clockIn(0L));
//    }
//
//    @Test
//    void clockOut_zero_employeeId_throws_IllegalArgumentException() {
//        assertThrows(IllegalArgumentException.class, () -> attendanceService.clockOut(0L));
//    }
//}
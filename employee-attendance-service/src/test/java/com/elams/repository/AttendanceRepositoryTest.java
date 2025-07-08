package com.elams.repository;

import com.elams.AttendanceServiceApplication;
import com.elams.entities.Attendance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ContextConfiguration(classes= {AttendanceServiceApplication.class})

public class AttendanceRepositoryTest {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private TestEntityManager entityManager;

    // findByEmployeeId tests
    @Test
    void findByEmployeeId_positive() {
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(LocalDateTime.now());
        entityManager.persist(attendance);

        List<Attendance> attendances = attendanceRepository.findByEmployeeId(1L);
        assertFalse(attendances.isEmpty());
        assertEquals(1L, attendances.get(0).getEmployeeId());
    }

    @Test
    void findByEmployeeId_negative() {
        List<Attendance> attendances = attendanceRepository.findByEmployeeId(1L);
        assertTrue(attendances.isEmpty());
    }

    @Test
    void findByEmployeeId_multipleMatches() {
        Attendance attendance1 = new Attendance();
        attendance1.setEmployeeId(1L);
        attendance1.setClockInTime(LocalDateTime.now());
        entityManager.persist(attendance1);

        Attendance attendance2 = new Attendance();
        attendance2.setEmployeeId(1L);
        attendance2.setClockInTime(LocalDateTime.now().minusHours(1));
        entityManager.persist(attendance2);

        List<Attendance> attendances = attendanceRepository.findByEmployeeId(1L);
        assertEquals(2, attendances.size());
        assertEquals(1L, attendances.get(0).getEmployeeId());
        assertEquals(1L, attendances.get(1).getEmployeeId());
    }
    // findByClockInTimeBetween tests

    @Test
    void findByClockInTimeBetween_positive() {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(now);
        entityManager.persist(attendance);

        List<Attendance> attendances = attendanceRepository.findByClockInTimeBetween(now.minusHours(1), now.plusHours(1));
        assertFalse(attendances.isEmpty());
    }

    @Test
    void findByClockInTimeBetween_negative() {
        LocalDateTime now = LocalDateTime.now();
        List<Attendance> attendances = attendanceRepository.findByClockInTimeBetween(now.plusDays(1), now.plusDays(2));
        assertTrue(attendances.isEmpty());
    }

    

    
    @Test
    void findByEmployeeIdAndClockInTimeBetween_positive() {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(now);
        entityManager.persist(attendance);

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(1L, now.minusHours(1), now.plusHours(1));
        assertFalse(attendances.isEmpty());
        assertEquals(1L, attendances.get(0).getEmployeeId());
    }

    @Test
    void findByEmployeeIdAndClockInTimeBetween_negative() {
        LocalDateTime now = LocalDateTime.now();
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(1L, now.plusDays(1), now.plusDays(2));
        assertTrue(attendances.isEmpty());
    }

    @Test
    void findByEmployeeIdAndClockInTimeBetween_noEmployeeMatches() {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(now);
        entityManager.persist(attendance);

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(2L, now.minusHours(1), now.plusHours(1));
        assertTrue(attendances.isEmpty());
    }

    // findByEmployeeIdAndClockOutTimeIsNull tests

    @Test
    void findByEmployeeIdAndClockOutTimeIsNull_positive() {
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(LocalDateTime.now());
        entityManager.persist(attendance);

        Optional<Attendance> optionalAttendance = attendanceRepository.findByEmployeeIdAndClockOutTimeIsNull(1L);
        assertTrue(optionalAttendance.isPresent());
        assertEquals(1L, optionalAttendance.get().getEmployeeId());
    }

    @Test
    void findByEmployeeIdAndClockOutTimeIsNull_negative() {
        Optional<Attendance> optionalAttendance = attendanceRepository.findByEmployeeIdAndClockOutTimeIsNull(1L);
        assertFalse(optionalAttendance.isPresent());
    }

    @Test
    void findByEmployeeIdAndClockOutTimeIsNull_clockOutSet() {
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(LocalDateTime.now());
        attendance.setClockOutTime(LocalDateTime.now().plusHours(1));
        entityManager.persist(attendance);

        Optional<Attendance> optionalAttendance = attendanceRepository.findByEmployeeIdAndClockOutTimeIsNull(1L);
        assertFalse(optionalAttendance.isPresent());
    }

    // countByEmployeeIdAndClockInTimeBetween tests

    @Test
    void countByEmployeeIdAndClockInTimeBetween_positive() {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance = new Attendance();
        attendance.setEmployeeId(1L);
        attendance.setClockInTime(now);
        entityManager.persist(attendance);

        List<Attendance> count = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(1L, now.minusHours(1), now.plusHours(1));
        assertEquals(1, count);
    }

    @Test
    void countByEmployeeIdAndClockInTimeBetween_negative() {
        LocalDateTime now = LocalDateTime.now();
        List<Attendance> count = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(1L, now.plusDays(1), now.plusDays(2));
        assertEquals(0, count);
    }

    @Test
    void countByEmployeeIdAndClockInTimeBetween_multipleMatches() {
        LocalDateTime now = LocalDateTime.now();
        Attendance attendance1 = new Attendance();
        attendance1.setEmployeeId(1L);
        attendance1.setClockInTime(now);
        entityManager.persist(attendance1);

        Attendance attendance2 = new Attendance();
        attendance2.setEmployeeId(1L);
        attendance2.setClockInTime(now.minusMinutes(30));
        entityManager.persist(attendance2);

        List<Attendance> count = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(1L, now.minusHours(1), now.plusHours(1));
        assertEquals(2, count);
    }
}
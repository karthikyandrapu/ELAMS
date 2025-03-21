package com.elams.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.elams.aop.AppLogger;
import com.elams.dtos.AttendanceDTO;
import com.elams.dtos.EmployeeDTO;
import com.elams.entities.Attendance;
import com.elams.exception.ClockInNotFoundException;
import com.elams.exception.InvalidEmployeeIdException;
import com.elams.feign.EmployeeServiceClient;
import com.elams.repository.AttendanceRepository;

/**
 * Implementation of the AttendanceService interface.
 * Provides business logic for managing employee attendance records.
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = AppLogger.getLogger(AttendanceServiceImpl.class);

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private EmployeeServiceClient employeeServiceClient;

    /**
     * Validates if the employee ID is valid and if the employee exists.
     *
     * @param employeeId The employee ID to validate.
     * @throws InvalidEmployeeIdException If the employee ID is invalid or the employee is not found.
     */
    private void validateEmployeeId(Long employeeId) {
        logger.info("Validating employee ID: {}", employeeId);
        if (employeeId == null || employeeId <= 0) {
            logger.warn("Invalid employee ID: {}", employeeId);
            throw new InvalidEmployeeIdException("Invalid employee ID");
        }

        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);
        if (employee == null) {
            logger.warn("Employee not found for ID: {}", employeeId);
            throw new InvalidEmployeeIdException("Employee not found");
        }
        logger.info("Employee ID: {} validated successfully.", employeeId);
    }

    /**
     * Records the clock-in time for an employee.
     *
     * @param employeeId The ID of the employee clocking in.
     * @return A ResponseEntity containing the AttendanceDTO representing the clock-in record.
     */
    @Override
    public ResponseEntity<AttendanceDTO> clockIn(Long employeeId) {
        logger.info("Clocking in employee ID: {}", employeeId);
        validateEmployeeId(employeeId);
        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);
        if (employee == null) {
            logger.warn("Employee not found when clocking in: {}", employeeId);
            return ResponseEntity.badRequest().body(null);
        }

        Attendance attendance = new Attendance();
        attendance.setEmployeeId(employeeId);
        attendance.setClockInTime(LocalDateTime.now());
        attendance = attendanceRepository.save(attendance);

        logger.info("Employee ID: {} clocked in successfully.", employeeId);
        return ResponseEntity.ok(mapToDTO(attendance));
    }

    /**
     * Records the clock-out time for an employee.
     *
     * @param employeeId The ID of the employee clocking out.
     * @return A ResponseEntity containing the AttendanceDTO representing the clock-out record.
     * @throws ClockInNotFoundException If no active clock-in record is found for the employee.
     */
    @Override
    public ResponseEntity<AttendanceDTO> clockOut(Long employeeId) {
        logger.info("Clocking out employee ID: {}", employeeId);
        validateEmployeeId(employeeId);
        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);
        if (employee == null) {
            logger.warn("Employee not found when clocking out: {}", employeeId);
            return ResponseEntity.badRequest().body(null);
        }

        Attendance attendance = attendanceRepository.findByEmployeeIdAndClockOutTimeIsNull(employeeId)
                .orElseThrow(() -> {
                    logger.warn("Clock in entry not found for employee ID: {}", employeeId);
                    return new ClockInNotFoundException("Clock in entry not found");
                });

        attendance.setClockOutTime(LocalDateTime.now());
        double workHours = Duration.between(attendance.getClockInTime(), attendance.getClockOutTime()).toMinutes() / 60.0;
        attendance.setWorkHours(workHours);

        attendance = attendanceRepository.save(attendance);
        logger.info("Employee ID: {} clocked out successfully.", employeeId);
        return ResponseEntity.ok(mapToDTO(attendance));
    }

    /**
     * Retrieves attendance records for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A ResponseEntity containing a list of AttendanceDTOs.
     */
    @Override
    public ResponseEntity<List<AttendanceDTO>> getAttendanceForEmployee(Long employeeId) {
        logger.info("Retrieving attendance records for employee ID: {}", employeeId);
        validateEmployeeId(employeeId);
        EmployeeDTO employee = employeeServiceClient.getEmployeeById(employeeId);
        if (employee == null) {
            logger.warn("Employee not found when retrieving attendance: {}", employeeId);
            return ResponseEntity.badRequest().body(null);
        }

        List<Attendance> attendanceList = attendanceRepository.findByEmployeeId(employeeId);
        logger.info("Found {} attendance records for employee ID: {}", attendanceList.size(), employeeId);
        return ResponseEntity.ok(mapToDTOList(attendanceList));
    }

    /**
     * Retrieves attendance records for the current day.
     *
     * @return A ResponseEntity containing a list of AttendanceDTOs.
     */
    @Override
    public ResponseEntity<List<AttendanceDTO>> getAttendanceForToday() {
        logger.info("Retrieving attendance records for today.");
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        List<Attendance> attendances = attendanceRepository.findByClockInTimeBetween(startOfDay, endOfDay);
        logger.info("Found {} attendance records for today.", attendances.size());
        return ResponseEntity.ok(mapToDTOList(attendances));
    }

    /**
     * Retrieves attendance records for a specific employee on a specific date/time.
     *
     * @param employeeId The ID of the employee.
     * @param date       The date/time for which to retrieve attendance records.
     * @return A ResponseEntity containing a list of AttendanceDTOs.
     * @throws IllegalArgumentException If the provided date is null.
     */
    @Override
    public ResponseEntity<List<AttendanceDTO>> getAttendanceForEmployeeAndDate(Long employeeId, LocalDateTime date) {
        logger.info("Retrieving attendance records for employee ID: {} on date: {}", employeeId, date);
        validateEmployeeId(employeeId);
        if (date == null) {
            logger.warn("Date cannot be null when retrieving attendance by date.");
            throw new IllegalArgumentException("Date cannot be null");
        }

        LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = date.toLocalDate().atTime(23, 59, 59);
        List<Attendance> attendanceList = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(employeeId, startOfDay, endOfDay);
        logger.info("Found {} attendance records for employee ID: {} on date: {}", attendanceList.size(), employeeId, date);
        return ResponseEntity.ok(mapToDTOList(attendanceList));
    }

    /**
     * Counts the number of attendance records for an employee within a given date/time range.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start date/time of the range.
     * @param endDate    The end date/time of the range.
     * @return The count of attendance records.
     */
    @Override
    public int countAttendance(Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("Counting attendance records for employee ID: {} between {} and {}", employeeId, startDate, endDate);
        validateEmployeeId(employeeId);
        int count = attendanceRepository.findByEmployeeIdAndClockInTimeBetween(employeeId, startDate.toLocalDate(), endDate.toLocalDate());
        logger.info("Found {} attendance records for employee ID: {} between {} and {}", count, employeeId, startDate, endDate);
        return count;
    }

    /**
     * Maps an Attendance entity to an AttendanceDTO.
     *
     * @param attendance The Attendance entity to map.
     * @return The corresponding AttendanceDTO.
     */
    private AttendanceDTO mapToDTO(Attendance attendance) {
        return new AttendanceDTO(
                attendance.getEmployeeId(),
                attendance.getClockInTime(),
                attendance.getClockOutTime(),
                attendance.getWorkHours()
        );
    }

    /**
     * Maps a list of Attendance entities to a list of AttendanceDTOs.
     *
     * @param attendanceList The list of Attendance entities to map.
     * @return The corresponding list of AttendanceDTOs.
     */
    private List<AttendanceDTO> mapToDTOList(List<Attendance> attendanceList) {
        return attendanceList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
package com.elams.service;

import com.elams.dtos.AttendanceDTO;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing employee attendance operations.
 * This interface defines the contract for clocking in/out, retrieving attendance records,
 * and counting attendance within a specified date range.
 */
public interface AttendanceService {

    /**
     * Records the clock-in time for an employee.
     *
     * @param employeeId The ID of the employee clocking in.
     * @return A ResponseEntity containing the AttendanceDTO representing the clock-in record.
     */
    ResponseEntity<AttendanceDTO> clockIn(Long employeeId);

    /**
     * Records the clock-out time for an employee.
     *
     * @param employeeId The ID of the employee clocking out.
     * @return A ResponseEntity containing the AttendanceDTO representing the clock-out record.
     */
    ResponseEntity<AttendanceDTO> clockOut(Long employeeId);

    /**
     * Retrieves attendance records for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A ResponseEntity containing a list of AttendanceDTOs.
     */
    ResponseEntity<List<AttendanceDTO>> getAttendanceForEmployee(Long employeeId);

    /**
     * Retrieves attendance records for the current day.
     *
     * @return A ResponseEntity containing a list of AttendanceDTOs.
     */
    ResponseEntity<List<AttendanceDTO>> getAttendanceForToday();

    /**
     * Counts the number of attendance records for an employee within a given date/time range.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start date/time of the range.
     * @param endDate    The end date/time of the range.
     * @return The count of attendance records.
     */
    int countAttendance(Long employeeId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Retrieves attendance records for a specific employee on a specific date/time.
     *
     * @param employeeId The ID of the employee.
     * @param date       The date/time for which to retrieve attendance records.
     * @return A ResponseEntity containing a list of AttendanceDTOs.
     */
    ResponseEntity<List<AttendanceDTO>> getAttendanceForEmployeeAndDate(Long employeeId, LocalDateTime date);
}
package com.elams.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.elams.entities.Attendance;

/**
 * Repository interface for managing Attendance entities.
 * This interface extends JpaRepository and provides methods for CRUD operations
 * and custom queries related to Attendance records.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Finds all attendance records for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return A list of Attendance entities.
     */
    List<Attendance> findByEmployeeId(Long employeeId);

    /**
     * Finds all attendance records within a given date/time range.
     *
     * @param startDateTime The start of the date/time range.
     * @param endDateTime   The end of the date/time range.
     * @return A list of Attendance entities.
     */
    List<Attendance> findByClockInTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Finds all attendance records for a given employee within a given date/time range.
     *
     * @param employeeId    The ID of the employee.
     * @param startDateTime The start of the date/time range.
     * @param endDateTime   The end of the date/time range.
     * @return A list of Attendance entities.
     */
    List<Attendance> findByEmployeeIdAndClockInTimeBetween(Long employeeId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * Counts the number of attendance records for a given employee within a given date range.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start date of the range.
     * @param endDate    The end date of the range.
     * @return The count of attendance records.
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.employeeId = :employeeId AND DATE(a.clockInTime) BETWEEN :startDate AND :endDate")
    int findByEmployeeIdAndClockInTimeBetween(Long employeeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds an attendance record for a given employee where the clock-out time is null, indicating an active clock-in.
     *
     * @param employeeId The ID of the employee.
     * @return An Optional containing the Attendance entity, or empty if not found.
     */
    Optional<Attendance> findByEmployeeIdAndClockOutTimeIsNull(Long employeeId);
}
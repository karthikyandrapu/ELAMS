package com.elams.repository;





import java.util.List;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import com.elams.entities.AttendanceReport;

/**
 * Repository interface for managing AttendanceReport entities.
 * This interface extends JpaRepository to provide CRUD operations and custom queries
 * for AttendanceReport entities.
 */
@Repository
public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long> {

    /**
     * Finds all attendance reports for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return A list of AttendanceReport entities for the given employee ID.
     */
	
    List<AttendanceReport> findByEmployeeId(Long employeeId);

    /**
     * Finds the latest attendance report for a given employee ID, ordered by report ID in descending order.
     *
     * @param employeeId The ID of the employee.
     * @return An Optional containing the latest AttendanceReport entity, or an empty Optional if none found.
     */
    @Query("SELECT ar FROM AttendanceReport ar WHERE ar.employeeId = :employeeId ORDER BY ar.reportId DESC LIMIT 1")
    Optional<AttendanceReport> findLatestByEmployeeId(@Param("employeeId") Long employeeId);

    /**
     * Finds an attendance report for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return An Optional containing the AttendanceReport entity, or an empty Optional if none found.
     */
    @Query("SELECT ar FROM AttendanceReport ar WHERE ar.employeeId = :employeeId")
    Optional<AttendanceReport> findByEmployeeIdSingle(@Param("employeeId") Long employeeId);
}

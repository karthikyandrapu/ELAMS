package com.elams.repository;

import com.elams.entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository interface for accessing and managing {@link Shift} entities.
 * This interface extends {@link JpaRepository} to provide standard JPA repository operations.
 */
@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    /**
     * Finds all shifts assigned to a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A list of {@link Shift} entities assigned to the employee.
     */
    List<Shift> findByEmployeeId(Long employeeId);

    /**
     * Finds all shifts assigned to employees with the given IDs.
     *
     * @param employeeIds A list of employee IDs.
     * @return A list of {@link Shift} entities assigned to the employees.
     */
    List<Shift> findByEmployeeIdIn(List<Long> employeeIds);

    /**
     * Checks if a shift exists for a specific employee on a given date.
     *
     * @param employeeId The ID of the employee.
     * @param shiftDate  The date of the shift.
     * @return {@code true} if a shift exists, {@code false} otherwise.
     */
    boolean existsByEmployeeIdAndShiftDate(Long employeeId, LocalDate shiftDate);

    /**
     * Finds all shifts on a specific date.
     *
     * @param shiftDate The date of the shifts.
     * @return A list of {@link Shift} entities on the specified date.
     */
    List<Shift> findByShiftDate(LocalDate shiftDate);

    /**
     * Checks if a shift exists for a specific employee on a given date and time.
     *
     * @param employeeId The ID of the employee.
     * @param shiftDate  The date of the shift.
     * @param shiftTime  The time of the shift.
     * @return {@code true} if a shift exists, {@code false} otherwise.
     */
    boolean existsByEmployeeIdAndShiftDateAndShiftTime(Long employeeId, LocalDate shiftDate, LocalTime shiftTime);

    /**
     * Finds a shift for a specific employee on a given date and time.
     *
     * @param employeeId The ID of the employee.
     * @param shiftDate  The date of the shift.
     * @param shiftTime  The time of the shift.
     * @return The {@link Shift} entity, or {@code null} if not found.
     */
    Shift findByEmployeeIdAndShiftDateAndShiftTime(Long employeeId, LocalDate shiftDate, LocalTime shiftTime);

    /**
     * Finds a shift for a specific employee on a given date.
     *
     * @param employeeId The ID of the employee.
     * @param shiftDate  The date of the shift.
     * @return The {@link Shift} entity, or {@code null} if not found.
     */
    Shift findByEmployeeIdAndShiftDate(Long employeeId, LocalDate shiftDate);
    
    /**
     * Retrieves a list of shifts for a specific employee that are scheduled on or after the given date.
     * The results are ordered by shift date in ascending order, and within the same date, by shift time in ascending order.
     *
     * @param employeeId The ID of the employee whose shifts are being retrieved.
     * @param shiftDate  The date from which to retrieve shifts (inclusive).
     * @return A list of {@link Shift} objects representing the employee's shifts.
     */
    List<Shift> findByEmployeeIdAndShiftDateGreaterThanEqualOrderByShiftDateAscShiftTimeAsc(Long employeeId, LocalDate shiftDate);

}
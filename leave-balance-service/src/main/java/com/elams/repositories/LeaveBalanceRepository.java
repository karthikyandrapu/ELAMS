package com.elams.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;

/**
 * Repository interface for {@link LeaveBalance} entities.
 * This interface extends {@link JpaRepository} to provide CRUD operations and custom queries for LeaveBalance entities.
 */
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {

    /**
     * Finds a LeaveBalance entity by employee ID and leave type.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @return An Optional containing the LeaveBalance entity, or empty if not found.
     */
    Optional<LeaveBalance> findByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);

    /**
     * Finds all LeaveBalance entities for a specific employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return A list of LeaveBalance entities.
     */
    List<LeaveBalance> findByEmployeeId(Long employeeId);

    /**
     * Checks if any LeaveBalance entities exist for a specific employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return true if LeaveBalance entities exist, false otherwise.
     */
    boolean existsByEmployeeId(Long employeeId);

    /**
     * Deletes all LeaveBalance entities for a specific employee ID.
     *
     * @param employeeId The ID of the employee.
     */
    void deleteByEmployeeId(Long employeeId);
}
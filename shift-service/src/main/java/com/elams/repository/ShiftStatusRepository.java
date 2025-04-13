package com.elams.repository;

import com.elams.entities.ShiftStatus;
import com.elams.enums.ShiftStatusType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing and managing {@link ShiftStatus} entities.
 * This interface extends {@link JpaRepository} to provide standard JPA repository operations.
 */
@Repository
public interface ShiftStatusRepository extends JpaRepository<ShiftStatus, Long> {

    /**
     * Finds a {@link ShiftStatus} entity by its shift ID.
     *
     * @param shiftId The ID of the shift.
     * @return The {@link ShiftStatus} entity, or {@code null} if not found.
     */
    ShiftStatus findByShiftId(Long shiftId);

    /**
     * Checks if a {@link ShiftStatus} entity exists for a given shift ID.
     *
     * @param shiftId The ID of the shift.
     * @return {@code true} if a {@link ShiftStatus} entity exists, {@code false} otherwise.
     */
    boolean existsByShiftId(Long shiftId);

    /**
     * Deletes a {@link ShiftStatus} entity by its shift ID.
     *
     * @param shiftId The ID of the shift.
     */
    void deleteByShiftId(Long shiftId);

    /**
     * Finds all {@link ShiftStatus} entities with a specific status.
     *
     * @param status The {@link ShiftStatusType} to filter by.
     * @return A list of {@link ShiftStatus} entities with the specified status.
     */
    List<ShiftStatus> findByStatus(ShiftStatusType status);

    /**
     * Finds all {@link ShiftStatus} entities with any of the specified statuses.
     *
     * @param statuses A list of {@link ShiftStatusType} statuses to filter by.
     * @return A list of {@link ShiftStatus} entities with any of the specified statuses.
     */
    List<ShiftStatus> findByStatusIn(List<ShiftStatusType> statuses);
    
    
    
    @Query("SELECT ss FROM ShiftStatus ss JOIN Shift s ON ss.shiftId = s.shiftId WHERE ss.status IN :statuses AND s.employeeId = :employeeId")
    List<ShiftStatus> findByStatusInAndEmployeeId(@Param("statuses") List<ShiftStatusType> statuses, @Param("employeeId") Long employeeId);

    @Query("SELECT ss FROM ShiftStatus ss JOIN Shift s ON ss.shiftId = s.shiftId WHERE ss.status = :status AND s.employeeId = :employeeId")
    List<ShiftStatus> findByStatusAndEmployeeId(@Param("status") ShiftStatusType status, @Param("employeeId") Long employeeId);
}

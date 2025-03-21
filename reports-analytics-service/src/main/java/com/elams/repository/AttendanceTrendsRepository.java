package com.elams.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.elams.entities.AttendanceTrends;

import jakarta.transaction.Transactional;

/**
 * Repository interface for managing AttendanceTrends entities.
 * This interface extends JpaRepository to provide CRUD operations
 * for AttendanceTrends entities.
 */
@Repository
public interface AttendanceTrendsRepository extends JpaRepository<AttendanceTrends, Long> {
	@Transactional
    void deleteByEmployeeId(Long employeeId);
}
package com.elams.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
	
Optional<LeaveBalance> findByEmployeeIdAndLeaveType(Long employeeId, LeaveType leaveType);
List<LeaveBalance> findByEmployeeId(Long employeeId);
}

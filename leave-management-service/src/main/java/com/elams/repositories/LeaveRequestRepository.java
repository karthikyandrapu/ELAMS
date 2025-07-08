
package com.elams.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.elams.entities.LeaveRequest;
import com.elams.entities.LeaveStatus;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    List<LeaveRequest> findByEmployeeIdAndStatus(Long employeeId, LeaveStatus status);

    List<LeaveRequest> findByEmployeeId(Long employeeId);

    List<LeaveRequest> findByEmployeeIdIn(List<Long> employeeIds); 

}

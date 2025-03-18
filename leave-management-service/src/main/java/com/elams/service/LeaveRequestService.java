package com.elams.service;


import com.elams.entities.LeaveRequest;
import com.elams.entities.LeaveType;
import com.elams.exception.LeaveRequestResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestService {

    ResponseEntity<LeaveRequest> requestLeave(Long employeeId, Long managerId, LeaveType leaveType, LocalDate startDate, LocalDate endDate);

    ResponseEntity<LeaveRequest> reviewLeaveRequest(Long leaveId, String decision, Long managerId) throws LeaveRequestResourceNotFoundException;

    ResponseEntity<LeaveRequest> leaveRequestStatusForEmployee(Long leaveId, Long employeeId) throws LeaveRequestResourceNotFoundException;

    ResponseEntity<List<LeaveRequest>> getAllLeaveRequests();

    ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployeeId(Long employeeId);

    ResponseEntity<List<LeaveRequest>> getLeaveRequestsByManagerId(Long managerId);
}
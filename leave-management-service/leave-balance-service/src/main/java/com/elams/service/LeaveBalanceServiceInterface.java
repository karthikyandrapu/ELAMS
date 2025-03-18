package com.elams.service;

import java.util.List;

import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveType;

public interface LeaveBalanceServiceInterface {
	LeaveBalanceDTO getLeaveBalance(Long employeeId,LeaveType leaveType);
	LeaveBalanceDTO updateLeaveBalance(Long employeeId,LeaveType leaveType,Double daysTaken);
	LeaveBalanceDTO createLeaveBalance(Long employeeId,LeaveType leaveType,Double balance);
	List<LeaveBalanceDTO>getAllLeaveBalanceOfEmployee(Long employeeId );
	boolean   hasSufficientBalance(Long employeeId,LeaveType leaveType,Double days);

}

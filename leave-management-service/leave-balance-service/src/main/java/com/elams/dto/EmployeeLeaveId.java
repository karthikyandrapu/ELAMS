package com.elams.dto;

import com.elams.entities.LeaveType;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor


public class EmployeeLeaveId {
  private Long  employeeId;
  private Double balance;
  private LeaveType leaveType;
}

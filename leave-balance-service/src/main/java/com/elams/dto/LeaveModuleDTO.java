package com.elams.dto;

import com.elams.entities.LeaveType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor


public class LeaveModuleDTO {
	
  @NotNull(message="Employee Id should be specified")
  private Long  employeeId;
  @PositiveOrZero(message="Balance should be greater than or equal to zero")
  private Double balance;
  @PositiveOrZero(message="Requested should not be negative or zero")
  private Double requestedDays;
 private LeaveType leaveType;
}

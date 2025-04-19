package com.elams.dto;

import java.time.LocalDate;


import com.elams.entities.LeaveStatus;
import com.elams.entities.LeaveType;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveRequestDTO {
	
	private Long leaveId;
	@NotNull(message = "Employee ID cannot be null")
	private Long employeeId;
	@NotNull(message = "LeaveType cannot be null")
	private LeaveType  leaveType;
	@NotNull(message = "Start Date cannot be null")
	private LocalDate startDate;
	@NotNull(message = "End Date cannot be null")
	private LocalDate  endDate;
	private LeaveStatus status;
    private EmployeesDTO employeesDTO;
	
}

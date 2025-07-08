package com.elams.dto;

import java.time.LocalDate;

import com.elams.entities.LeaveType;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@NoArgsConstructor
@Getter
@Setter

public class EmployeeDetailsDTO {
	private Long id;
	private Long managerId;
	private LeaveType  leaveType;
	private LocalDate startDate;
	private LocalDate  endDate;

}

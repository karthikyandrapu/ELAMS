package com.elams.dto;


import com.elams.entities.LeaveType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceDTO {

	
	private Long id;
	@NotNull(message="Employee ID Should not be null")

    Long employeeId;
	@NotNull(message="Leave Type Should Be Specified")
	
	private LeaveType leaveType;
	@NotNull(message="Balance should not be null")
	@PositiveOrZero(message="Balance should be greater than or equal to zero")
	private Double balance;
	
}

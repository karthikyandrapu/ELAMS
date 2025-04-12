package com.elams.dto;

import com.elams.entities.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class LeaveBalanceCheckDTO {
	    private Long employeeId;
	    private LeaveType leaveType;
	    private Double days;
}

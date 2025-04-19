package com.elams.dto;

import com.elams.enums.EmployeeRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeesDTO {
	private Long id;
	private String name;
	
	private EmployeeRole role;
	private Long managerId;
	}
	



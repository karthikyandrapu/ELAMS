package com.elams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSignUpDTO {
	Long empId;
	String empName;
	String empRole; 
	String userName;
	String password;

}

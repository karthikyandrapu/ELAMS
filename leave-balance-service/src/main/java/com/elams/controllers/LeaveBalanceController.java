package com.elams.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elams.dto.EmployeeLeaveId;
import com.elams.dto.LeaveBalanceCheckDTO;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.dto.LeaveBalanceUpdateDTO;
import com.elams.service.LeaveBalanceServiceInterface;

@RestController
@RequestMapping("api/leave-balance")
@CrossOrigin(origins = "http://localhost:4200/")
public class LeaveBalanceController {
	
	@Autowired
	private LeaveBalanceServiceInterface leaveBalanceService;
	
//	@GetMapping("/l")
//	public Double getLeaveBalance(@RequestBody EmployeeLeaveId employee)
//	{
//		
//		LeaveBalanceDTO dto= leaveBalanceService.getLeaveBalance(employee.getEmpId(), employee.getLeaveType());
//		return dto.getBalance();
//    }
	@GetMapping("/getBalance")
    public  Double getLeaveBalance(@RequestBody EmployeeLeaveId employee) {
        LeaveBalanceDTO dto = leaveBalanceService.getLeaveBalance(employee.getEmployeeId(), employee.getLeaveType());
        return dto.getBalance();
    }
	
	 
	
//	@PutMapping("/update")
//	public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@RequestBody EmployeeLeaveId employee)
//	{
//		LeaveBalanceDTO dto=leaveBalanceService.updateLeaveBalance(employee.getEmployeeId(), employee.getLeaveType(), employee.getBalance());
//		return ResponseEntity.ok(dto);
//		
//		
//		
//		
//	
	@PutMapping("/update")
    public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@RequestBody LeaveBalanceUpdateDTO leaveBalanceUpdateDTO) {
        LeaveBalanceDTO dto = leaveBalanceService.updateLeaveBalance(
                leaveBalanceUpdateDTO.getEmployeeId(),
                leaveBalanceUpdateDTO.getLeaveType(),
                leaveBalanceUpdateDTO.getDays()
        );
        return ResponseEntity.ok(dto);
    }
	
	
	@PostMapping("/create")
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@RequestBody EmployeeLeaveId employeeId)
    {
		LeaveBalanceDTO dto=leaveBalanceService.createLeaveBalance(employeeId.getEmployeeId(), employeeId.getLeaveType(), employeeId.getBalance());
		return ResponseEntity.ok(dto);
			
    }
	
	
	@GetMapping("/all")
	public ResponseEntity<List<LeaveBalanceDTO>> getAllLeaveBalancesForEmployees(@RequestBody EmployeeLeaveId employeeId)
	{
		List<LeaveBalanceDTO> dtoList=leaveBalanceService.getAllLeaveBalanceOfEmployee(employeeId.getEmployeeId());
		return ResponseEntity.ok(dtoList);

		 
	}
	
	
	 @PostMapping("/sufficient")
	    public String checkIfBalanceIsSufficient(@RequestBody LeaveBalanceCheckDTO leaveBalanceCheckDTO) {
		 System.out.println(leaveBalanceCheckDTO.getEmployeeId());
		 System.out.println(leaveBalanceCheckDTO.getLeaveType());
		 System.out.println(leaveBalanceCheckDTO.getDays());
	        Boolean status = leaveBalanceService.hasSufficientBalance(
	                leaveBalanceCheckDTO.getEmployeeId(),
	                leaveBalanceCheckDTO.getLeaveType(),
	                leaveBalanceCheckDTO.getDays()
	        );
	        return status ? "true" : "false";
	    }
	
	
	

}

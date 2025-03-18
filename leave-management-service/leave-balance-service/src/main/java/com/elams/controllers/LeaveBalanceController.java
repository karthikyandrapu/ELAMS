package com.elams.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.elams.dto.EmployeeLeaveId;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveType;
import com.elams.service.LeaveBalanceServiceInterface;

@RestController
@RequestMapping("api/leave-balance")
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
	@GetMapping("/{empId}/{leaveType}")
    public  Double getLeaveBalance(@PathVariable Long employeeId, @PathVariable LeaveType leaveType) {
        LeaveBalanceDTO dto = leaveBalanceService.getLeaveBalance(employeeId, leaveType);
        return dto.getBalance();
    }
	
	
	
	@PutMapping("/update")
	public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@RequestBody EmployeeLeaveId employee)
	{
		LeaveBalanceDTO dto=leaveBalanceService.updateLeaveBalance(employee.getEmployeeId(), employee.getLeaveType(), employee.getBalance());
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
//	@GetMapping("/sufficient")
//    public String checkIfBalanceIsSufficient(
//            @RequestParam("employeeId") Long employeeId,
//            @RequestParam("leaveType") LeaveType leaveType,
//            @RequestParam("days") Double days) {
//        Boolean status = leaveBalanceService.hasSufficientBalance(employeeId, leaveType, days);
//        if (status) {
//            return "true";
//        } else {
//            return "false";
//        }
//    }
	@GetMapping("/sufficient")
    public String checkIfBalanceIsSufficient(
            @RequestParam("employeeId") Long employeeId,
            @RequestParam("leaveType") LeaveType leaveType,
            @RequestParam("days") Double days) {
        Boolean status = leaveBalanceService.hasSufficientBalance(employeeId, leaveType, days);
        return status ? "true" : "false";
    }
//	@GetMapping("/sufficient")
//	public  String  checkIfBalanceIsSufficient(
//			@RequestParam("employeeId") Long employeeId,
//            @RequestParam("leaveType") LeaveType leaveType,
//            @RequestParam("days") Double days)
//	{
//		Boolean status=leaveBalanceService.hasSufficientBalance(employeeId, leaveType, days);
//		if(status)
//		{
//			return "Balance is sufficient";
//		}
//		else
//		{
//			return "Invalid input";
//		}
// 
//	}
	
	

}

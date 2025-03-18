package com.elams.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.elams.dto.LeaveBalanceCheckDTO;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.dto.LeaveBalanceUpdateDTO;
import com.elams.entities.LeaveType;

@FeignClient(name = "leave-balance-service")
public interface LeaveBalanceClient {

    @GetMapping("api/leave-balance/")
    LeaveBalanceDTO getLeaveBalance(@RequestParam("employeeId") Long employeeId, @RequestBody LeaveType leaveType);

    @GetMapping("api/leave-balance/employee/{employeeId}")
    List<LeaveBalanceDTO> getAllLeaveBalanceOfEmployee(@PathVariable("employeeId") Long employeeId);

	

//    @PutMapping("api/leave-balance/update")
//    void updateLeaveBalance(@RequestParam("employeeId") Long employeeId, @RequestParam("leaveType") LeaveType leaveType ,@RequestParam("days") Double daysTaken) ;
    @PutMapping("/api/leave-balance/update")
    void updateLeaveBalance(@RequestBody LeaveBalanceUpdateDTO leaveBalanceUpdateDTO);
//    @GetMapping("/leave-balance/sufficient")
//  String hasSufficientBalance(@RequestParam("employeeId") Long employeeId, @RequestParam("leaveType") LeaveType leaveType,@RequestParam("days") Double days);
    //@GetMapping("api/leave-balance/sufficient")
//    String hasSufficientBalance(@RequestParam("employeeId") Long employeeId, @RequestParam("leaveType") LeaveType leaveType, @RequestParam("days") Double days);
    @PostMapping("/api/leave-balance/sufficient")
    String hasSufficientBalance(@RequestBody LeaveBalanceCheckDTO leaveBalanceCheckDTO);
}


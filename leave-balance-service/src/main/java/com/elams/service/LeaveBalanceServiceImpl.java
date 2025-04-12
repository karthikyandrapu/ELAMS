package com.elams.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.demo.exceptions.ResourceNotFoundException;
import com.elams.defaultvalues.SetDefaultLeaveBalances;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;
import com.elams.repositories.LeaveBalanceRepository;

import jakarta.transaction.Transactional;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceServiceInterface {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    
//    @Autowired
//    private SetDefaultLeaveBalances setDefaultLeaveBalances;

    @Override
    public LeaveBalanceDTO getLeaveBalance(Long employeeId, LeaveType leaveType) {
        Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, leaveType);
        LeaveBalance balance;
        
        if (leaveBalance.isPresent()) {
            balance = leaveBalance.get();
        } 
        else {
        	
        	
           balance=createDefaultLeaveBalance(employeeId, leaveType);
           
        }
        System.out.println(balance.getBalance());
        LeaveBalanceDTO balanceDTO=new LeaveBalanceDTO();
        balanceDTO.setEmployeeId(balance.getEmployeeId());
        balanceDTO.setLeaveType(balance.getLeaveType());
        balanceDTO.setBalance(balance.getBalance());
        
        return balanceDTO;
        

    }
//    
//    public Double getDefaultLeaveBalances(LeaveType leaveType)
//	{
//		switch(leaveType)
//		{
//		case SICK:
//			return 5.0;
//		case VACATION:
//			return 8.0;
//		case CASUAL:
//			return 3.0;
//		case OTHER:
//			return 3.0;
//		default:
//			return 0.0;
//			
//		}
//	}
    
    
    public  LeaveBalance createDefaultLeaveBalance(Long employeeId,LeaveType leaveType)
    {
    	System.out.println("******Create default leave balance");
    	LeaveBalance newBalance=new LeaveBalance();
    	System.out.println("ededefdfw"+newBalance);
    	newBalance.setEmployeeId(employeeId);
    	newBalance.setLeaveType(leaveType);
    	newBalance.setBalance(leaveType.getDefaultLeaves());
    	System.out.println(newBalance);
    	System.out.println("in default");
    	return leaveBalanceRepository.save(newBalance);
    }
    @Scheduled(cron = "0 0 0 L 12 ?")
    @Transactional
    public void resetMonthlyLeaveBalance()
    {
    	List<LeaveBalance> allBalances=leaveBalanceRepository.findAll();
    	for(LeaveBalance balance:allBalances)
    	{
    		balance.setBalance(balance.getLeaveType().getDefaultLeaves());
    	}
    	
    	leaveBalanceRepository.saveAll(allBalances);
    }

    @Override
    @Transactional
    public LeaveBalanceDTO updateLeaveBalance(Long employeeId, LeaveType leaveType, Double daysTaken) {
        Optional<LeaveBalance> leaveBalanceOptional = leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, leaveType);
        if (leaveBalanceOptional.isPresent()) {
            LeaveBalance leaveBalance = leaveBalanceOptional.get();
            if (leaveBalance.getBalance() < daysTaken) {
                throw new IllegalArgumentException("Insufficient Leave Balance");
            } else {
                leaveBalance.setBalance(leaveBalance.getBalance() - daysTaken);
                LeaveBalance updated = leaveBalanceRepository.save(leaveBalance);
                LeaveBalanceDTO dto = new LeaveBalanceDTO();
                dto.setId(updated.getId());
                dto.setEmployeeId(updated.getEmployeeId());
                dto.setLeaveType(updated.getLeaveType());
                dto.setBalance(updated.getBalance());
                return dto;
            }
        } else {
            throw new ResourceNotFoundException("Employee record not found");
        }
    }

    @Override
    public LeaveBalanceDTO createLeaveBalance(Long employeeId, LeaveType leaveType, Double balance) {
        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setEmployeeId(employeeId);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setBalance(balance);
        LeaveBalance saved = leaveBalanceRepository.save(leaveBalance);
        LeaveBalanceDTO dto = new LeaveBalanceDTO();
        dto.setId(saved.getId());
        dto.setEmployeeId(saved.getEmployeeId());
        dto.setLeaveType(saved.getLeaveType());
        dto.setBalance(saved.getBalance());
        return dto;
    }

    @Override
    public List<LeaveBalanceDTO> getAllLeaveBalanceOfEmployee(Long employeeId) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(employeeId);
        List<LeaveBalanceDTO> dtos = new ArrayList<>();
        for (LeaveBalance lb : balances) {
            LeaveBalanceDTO dto = new LeaveBalanceDTO();
            dto.setId(lb.getId());
            dto.setEmployeeId(lb.getEmployeeId());
            dto.setLeaveType(lb.getLeaveType());
            dto.setBalance(lb.getBalance());
            dtos.add(dto);
        }
        return dtos;
    }
    
    
    @Override
    public boolean hasSufficientBalance(Long employeeId, LeaveType leaveType, Double days) {

        Optional<LeaveBalance> employeeOptional = leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, leaveType);

        if (employeeOptional.isPresent()) {
        	System.out.println("in if block");
            LeaveBalance employeeBalance = employeeOptional.get();
            System.out.println(employeeBalance.getBalance());
            System.out.println(employeeBalance.getBalance() >= days && days > 0);
            return employeeBalance.getBalance() >= days && days > 0;
        } else {
        	
        	System.out.println("In else block");
            // No existing LeaveBalance found, create a default and check its balance
            LeaveBalance defaultBalance = createDefaultLeaveBalance(employeeId, leaveType);
            System.out.println("after default");
            System.out.println(defaultBalance);
            return defaultBalance.getBalance() >= days && days > 0;
        }
    }
}
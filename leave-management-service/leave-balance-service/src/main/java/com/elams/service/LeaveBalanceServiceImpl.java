package com.elams.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.demo.exceptions.ResourceNotFoundException;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;
import com.elams.repositories.LeaveBalanceRepository;

import jakarta.transaction.Transactional;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceServiceInterface {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public LeaveBalanceDTO getLeaveBalance(Long employeeId, LeaveType leaveType) {
        Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, leaveType);
        if (leaveBalance.isPresent()) {
            LeaveBalance lb = leaveBalance.get();
            LeaveBalanceDTO dto = new LeaveBalanceDTO();
            dto.setId(lb.getId());
            dto.setEmployeeId(lb.getEmployeeId());
            dto.setLeaveType(lb.getLeaveType());
            dto.setBalance(lb.getBalance());
            return dto;
        } else {
            throw new ResourceNotFoundException("Employee and balance not found");
        }
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
    public boolean   hasSufficientBalance(Long employeeId,LeaveType leaveType,Double days)
    {
    	Optional<LeaveBalance> employee=leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId,leaveType);
    	if(employee.get().getBalance()>=days && days>0)
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    			
    	}
    	
    }
 
    
}
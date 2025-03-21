package com.elams.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.demo.exceptions.ResourceNotFoundException;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;
import com.elams.repositories.LeaveBalanceRepository;

import jakarta.transaction.Transactional;

/**
 * Service implementation for managing leave balance operations.
 * This service provides methods for retrieving, updating, creating, and deleting leave balance records.
 */
@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceServiceInterface {

    private final LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    public LeaveBalanceServiceImpl(LeaveBalanceRepository leaveBalanceRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
    }

    /**
     * Retrieves the leave balance for a specific employee and leave type.
     * If the leave balance does not exist, it creates a default leave balance.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @return The leave balance DTO.
     */
    @Override
    public LeaveBalanceDTO getLeaveBalance(Long employeeId, LeaveType leaveType) {
        Optional<LeaveBalance> leaveBalance = leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, leaveType);
        LeaveBalance balance;

        if (leaveBalance.isPresent()) {
            balance = leaveBalance.get();
        } else {
            balance = createDefaultLeaveBalance(employeeId, leaveType);
        }
        LeaveBalanceDTO balanceDTO = new LeaveBalanceDTO();
        balanceDTO.setEmployeeId(balance.getEmployeeId());
        balanceDTO.setLeaveType(balance.getLeaveType());
        balanceDTO.setBalance(balance.getBalance());

        return balanceDTO;
    }

    /**
     * Creates a default leave balance record for an employee and leave type.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @return The created leave balance.
     */
    public LeaveBalance createDefaultLeaveBalance(Long employeeId, LeaveType leaveType) {
        LeaveBalance newBalance = new LeaveBalance();
        newBalance.setEmployeeId(employeeId);
        newBalance.setLeaveType(leaveType);
        newBalance.setBalance(leaveType.getDefaultLeaves());
        return leaveBalanceRepository.save(newBalance);
    }

    /**
     * Resets the yearly leave balance for all employees.
     * This method is scheduled to run at the end of each year.
     */
    @Scheduled(cron = "0 0 0 L 12 ?")
    @Transactional
    public void resetYearlyLeaveBalance() {
        List<LeaveBalance> allBalances = leaveBalanceRepository.findAll();
        for (LeaveBalance balance : allBalances) {
            balance.setBalance(balance.getLeaveType().getDefaultLeaves());
        }

        leaveBalanceRepository.saveAll(allBalances);
    }

    /**
     * Updates the leave balance for an employee after taking leave.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @param daysTaken  The number of days taken.
     * @return The updated leave balance DTO.
     * @throws IllegalArgumentException if the employee has insufficient leave balance.
     * @throws ResourceNotFoundException if the employee record is not found.
     */
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
        } 
        else {
            throw new ResourceNotFoundException("Employee record not found");
        }
    }

    /**
     * Creates a new leave balance record for an employee.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @param balance    The initial leave balance.
     * @return The created leave balance DTO.
     */
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

    /**
     * Retrieves all leave balances for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A list of leave balance DTOs.
     * @throws ResourceNotFoundException if no employee leave balance is found.
     */
    @Override
    public List<LeaveBalanceDTO> getAllLeaveBalanceOfEmployee(Long employeeId) {
        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeId(employeeId);

        if (!balances.isEmpty()) {
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
        } else {
            throw new ResourceNotFoundException("Employee balance records not found");
        }
    }

    /**
     * Checks if an employee has sufficient leave balance for the requested leave.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @param days       The number of days requested.
     * @return true if the employee has sufficient balance, false otherwise.
     */
    @Override
    public boolean hasSufficientBalance(Long employeeId, LeaveType leaveType, Double days) {
        Optional<LeaveBalance> employee = leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, leaveType);
        
        return (employee.get().getBalance() >= days && days > 0) ;
    }
       
   

    /**
     * Deletes all leave balances for a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return true if the deletion is successful.
     * @throws ResourceNotFoundException if no leave balances are found for the employee.
     */
    @Override
    @Transactional
    public boolean deleteAllLeaveBalancesForEmployee(Long employeeId) {

        if (!leaveBalanceRepository.existsByEmployeeId(employeeId)) {
            throw new ResourceNotFoundException("No leave balances found for Employee ID:" + employeeId);
        }
        leaveBalanceRepository.deleteByEmployeeId(employeeId);
        return true;
    }
}
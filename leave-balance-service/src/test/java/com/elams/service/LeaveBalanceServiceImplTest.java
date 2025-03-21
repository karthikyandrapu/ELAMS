package com.elams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.demo.exceptions.ResourceNotFoundException;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveBalance;
import com.elams.entities.LeaveType;
import com.elams.repositories.LeaveBalanceRepository;

class LeaveBalanceServiceImplTest {
	
	
	@Mock
	private LeaveBalanceRepository leaveBalanceRepository;
	
	@InjectMocks
	private LeaveBalanceServiceImpl  leaveBalanceService;

	@BeforeEach
	void setUp()  {
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() {
		leaveBalanceRepository=null;
		leaveBalanceService=null;
	}

	@Test
	void test_getLeaveBalance_positive() {
		LeaveBalance leaveBalance =new LeaveBalance();
		leaveBalance.setEmployeeId(100L);
		leaveBalance.setLeaveType(LeaveType.VACATION);
		leaveBalance.setBalance(0.0);
		
		when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(100L, LeaveType.VACATION)).thenReturn(Optional.of(leaveBalance));
		LeaveBalanceDTO actual=leaveBalanceService.getLeaveBalance(100L, LeaveType.VACATION);
		verify(leaveBalanceRepository,times(1)).findByEmployeeIdAndLeaveType(100L, LeaveType.VACATION);
		assertEquals(0.0,actual.getBalance());
		

		
	}
	
	@Test
	@DisplayName("")
	void test_getLeaveBalance_DefaultBalance()
	{
		
		
		when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(101L, LeaveType.VACATION)).thenReturn(Optional.empty());
		LeaveBalance balance=new LeaveBalance();
		balance.setEmployeeId(1001L);
		balance.setBalance(8D);
		
		
		when(leaveBalanceRepository.save(any())).thenReturn(balance);
		LeaveBalanceDTO actual=leaveBalanceService.getLeaveBalance(101L, LeaveType.VACATION);
		assertEquals(LeaveType.VACATION.getDefaultLeaves(),actual.getBalance());

		
	}
	
	
	@Test
	void testUpdateLeaveBalance_WhenBalanceIsSufficient()
	{
		LeaveBalance leaveBalance=new LeaveBalance();
		leaveBalance.setEmployeeId(100L);
		leaveBalance.setLeaveType(LeaveType.VACATION);
		leaveBalance.setBalance(10.0);
		
		when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(100L,LeaveType.VACATION)).thenReturn(Optional.of(leaveBalance));
		when(leaveBalanceRepository.save(any())).thenReturn(leaveBalance);

		LeaveBalanceDTO   deductedBalance =leaveBalanceService.updateLeaveBalance(100L,LeaveType.VACATION , 2.0);
		assertEquals(8.0,deductedBalance.getBalance());
		
	}
	
	
	@Test
	void testUpdateLeaveBalance_WhenBalanceIsNotSufficent()
	{
		LeaveBalance leaveBalance=new LeaveBalance();
		leaveBalance.setEmployeeId(100L);
		leaveBalance.setLeaveType(LeaveType.VACATION);
		leaveBalance.setBalance(10.0);
		
		when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(100L,LeaveType.VACATION)).thenReturn(Optional.of(leaveBalance));
		when(leaveBalanceRepository.save(any())).thenReturn(leaveBalance);
       assertThrows(IllegalArgumentException.class,()->leaveBalanceService.updateLeaveBalance(100L,LeaveType.VACATION , 20.0));
		
	}
	@Test
      void testUpdateLeaveBalance_ResourceNotFound() {
	    when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(anyLong(), any(LeaveType.class)))
	        .thenReturn(Optional.empty());

	    assertThrows(ResourceNotFoundException.class, () -> 
	        leaveBalanceService.updateLeaveBalance(1L, LeaveType.SICK, 2.0));
	}
	
	 @Test
	void testCreateLeaveBalance_CreateSuccessfully()
	{
		LeaveBalance leaveBalance=new LeaveBalance();
		leaveBalance.setEmployeeId(100L);
		leaveBalance.setLeaveType(LeaveType.VACATION);
		leaveBalance.setBalance(10.0);
		when(leaveBalanceRepository.save(any())).thenReturn(leaveBalance);
		LeaveBalanceDTO actual=leaveBalanceService.createLeaveBalance(1002L, LeaveType.SICK, 5.0);
		assertEquals(10.0,actual.getBalance());
		
	}
	 
	 @Test
	 void testGetAllLeaveBalances_WhenEmployeeHasBalances()
	 {
		 LeaveBalance leaveBalance1 = new LeaveBalance();
	        leaveBalance1.setEmployeeId(100L);
	        leaveBalance1.setLeaveType(LeaveType.VACATION);
	        leaveBalance1.setBalance(10.0);

	        LeaveBalance leaveBalance2 = new LeaveBalance();
	        leaveBalance2.setEmployeeId(100L);
	        leaveBalance2.setLeaveType(LeaveType.SICK);
	        leaveBalance2.setBalance(5.0);

	        List<LeaveBalance> leaveBalanceList = new ArrayList();
	        leaveBalanceList.add(leaveBalance1);
	        leaveBalanceList.add(leaveBalance2);
	        when(leaveBalanceRepository.findByEmployeeId(100L)).thenReturn(leaveBalanceList);	
	        List<LeaveBalanceDTO> actual=leaveBalanceService.getAllLeaveBalanceOfEmployee(100L);
	        assertEquals(2, actual.size());
	 }
	 
	 
	    @Test
	    void testGetAllLeaveBalances_WhenEmployeesHasNoBalance() {
	        // Arrange
	        Long employeeId = 1001L;
	        List<LeaveBalance> emptyList = new ArrayList<>();
	        when(leaveBalanceRepository.findByEmployeeId(employeeId)).thenReturn(emptyList);

	        
	        assertThrows(ResourceNotFoundException.class, () -> leaveBalanceService.getAllLeaveBalanceOfEmployee(employeeId));
	    }
	 
	 
	 @Test
	 void testHasSufficientBalance_WhenBalanceIsSufficient()
	 {
		 LeaveBalance leaveBalance=new LeaveBalance();
			leaveBalance.setEmployeeId(100L);
			leaveBalance.setLeaveType(LeaveType.VACATION);
			leaveBalance.setBalance(10.0);
			when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(100L,LeaveType.VACATION )).thenReturn(Optional.of(leaveBalance));
			boolean status=leaveBalanceService.hasSufficientBalance(100L,LeaveType.VACATION  , 2.0);
			assertTrue(status);
		 
	 }
	 
	 
	 @Test
	 void testHasSufficientBalance_WhenBalanceIsNotSufficient()
	 {
		 LeaveBalance leaveBalance=new LeaveBalance();
			leaveBalance.setEmployeeId(100L);
			leaveBalance.setLeaveType(LeaveType.VACATION);
			leaveBalance.setBalance(2.0);
			when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(100L,LeaveType.VACATION )).thenReturn(Optional.of(leaveBalance));
			boolean status=leaveBalanceService.hasSufficientBalance(100L,LeaveType.VACATION  , 5.0);
			assertFalse(status);
		 
	 }
	 
	 @Test
	 void testRestYearlyLeaveBalance_ResetToDefault()
	 {
		    LeaveBalance leaveBalance1 = new LeaveBalance();
	        leaveBalance1.setEmployeeId(100L);
	        leaveBalance1.setLeaveType(LeaveType.VACATION);
	        leaveBalance1.setBalance(1.0);

	        LeaveBalance leaveBalance2 = new LeaveBalance();
	        leaveBalance2.setEmployeeId(102L);
	        leaveBalance2.setLeaveType(LeaveType.SICK);
	        leaveBalance2.setBalance(3.0);
	        
	        LeaveBalance leaveBalance3 = new LeaveBalance();
	        leaveBalance3.setEmployeeId(103L);
	        leaveBalance3.setLeaveType(LeaveType.CASUAL);
	        leaveBalance3.setBalance(4.0);
		   
	        List<LeaveBalance> allBalances=new ArrayList<>();
	        allBalances.add(leaveBalance1);
	        allBalances.add(leaveBalance2);
	        allBalances.add(leaveBalance3);
	        
	        when(leaveBalanceRepository.findAll()).thenReturn(allBalances);
	        leaveBalanceService.resetYearlyLeaveBalance();
	        assertEquals(LeaveType.VACATION.getDefaultLeaves(), leaveBalance1.getBalance());
	        assertEquals(LeaveType.SICK.getDefaultLeaves(), leaveBalance2.getBalance());
	        assertEquals(LeaveType.CASUAL.getDefaultLeaves(), leaveBalance3.getBalance());
}   
	 
	 @Test
	 void testDeleteAllLeaveBalancesForEmployee_Success()
	 {
		 Long employeeId=1001L;
		 when(leaveBalanceRepository.existsByEmployeeId(employeeId)).thenReturn(true);
		 doNothing().when(leaveBalanceRepository).deleteByEmployeeId(employeeId);
		 boolean result=leaveBalanceService.deleteAllLeaveBalancesForEmployee(employeeId);
		 assertTrue(result);
		 
	}
	 
	 @Test
	 void testDeleteAllLeaveBalancesForEmployee_NotFound()
	 {
		 Long employeeId=1001L;
		 when(leaveBalanceRepository.existsByEmployeeId(employeeId)).thenReturn(false);
		 Exception exception=assertThrows(ResourceNotFoundException.class, ()->
		 leaveBalanceService.deleteAllLeaveBalancesForEmployee(employeeId));
		 assertEquals("No leave balances found for Employee ID:1001",exception.getMessage());
		 
	}
	 
	 
}

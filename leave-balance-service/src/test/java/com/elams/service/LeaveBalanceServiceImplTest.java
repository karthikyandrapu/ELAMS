package com.elams.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
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
	 
//	 @Test
//	 void testGetAllLeaveBalances_WhenEmployeeHasBalances()
//	 {
//			LeaveBalance leaveBalance1=new LeaveBalance();
//			leaveBalance1.setEmployeeId(100L);
//			leaveBalance1.setLeaveType(LeaveType.VACATION);
//			leaveBalance1.setBalance(10.0);
//			
//			LeaveBalance leaveBalance2=new LeaveBalance();
//			leaveBalance2.setEmployeeId(100L);
//			leaveBalance2.setLeaveType(LeaveType.SICK);
//			leaveBalance2.setBalance(5.0);
//			
//			when(leaveBalanceRepository.findByEmployeeId(100l)).thenReturn(Arrays.asList(leaveBalance1,leaveBalance2));
//	 }
	
	
	

}

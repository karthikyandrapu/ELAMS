package com.elams.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.demo.exceptions.ResourceNotFoundException;
import com.elams.controllers.LeaveBalanceController;
import com.elams.dto.LeaveModuleDTO;
import com.elams.dto.LeaveBalanceDTO;
import com.elams.entities.LeaveType;
import com.elams.globalexceptionhandler.GlobalExceptionHandler;
import com.elams.service.LeaveBalanceServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

//@SpringBootTest(classes = LeaveBalanceServiceApplication.class)
//@WebMvcTest
//@EnableAspectJAutoProxy
class LeaveBalanceControllerTest {

	
	@Mock
	private LeaveBalanceServiceImpl leaveBalanceService;
	
	@InjectMocks
	private LeaveBalanceController  leaveBalanceController;
	
	private MockMvc mockMvc;
	
	@Autowired
	private LocalValidatorFactoryBean validator;
	
	
	@BeforeEach
	void setUp()  {
		MockitoAnnotations.openMocks(this);
		mockMvc=MockMvcBuilders.standaloneSetup(leaveBalanceController).build();
		mockMvc = MockMvcBuilders.standaloneSetup(leaveBalanceController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
	}

	@AfterEach
	void tearDown()  {
		
		leaveBalanceService =null;
		leaveBalanceController=null;
		mockMvc=null;
	}

	
	private static String asJsonString(final Object obj)
	{
		try
		{
			return new ObjectMapper().writeValueAsString(obj);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	 @Test
	    void testGetLeaveBalance_positive() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	        request.setLeaveType(LeaveType.VACATION);

	        LeaveBalanceDTO leaveBalanceDTO = new LeaveBalanceDTO();
	        leaveBalanceDTO.setEmployeeId(1001L);
	        leaveBalanceDTO.setLeaveType(LeaveType.VACATION);
	        leaveBalanceDTO.setBalance(10.0);

	        when(leaveBalanceService.getLeaveBalance(any(), any())).thenReturn(leaveBalanceDTO);

	        mockMvc.perform(get("/api/leave-balance/getBalance")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk());
	    }

	    @Test
	    void testGetLeaveBalance_negative_ShouldReturnDefaultBalance() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	        request.setLeaveType(LeaveType.VACATION);

	        // Simulate the service returning a default leave balance instead of throwing an exception
	        LeaveBalanceDTO defaultLeaveBalance = new LeaveBalanceDTO();
	        defaultLeaveBalance.setEmployeeId(1001L);
	        defaultLeaveBalance.setLeaveType(LeaveType.VACATION);
	        defaultLeaveBalance.setBalance(LeaveType.VACATION.getDefaultLeaves()); // Should return the default

	        when(leaveBalanceService.getLeaveBalance(any(), any())).thenReturn(defaultLeaveBalance);

	        MvcResult result = mockMvc.perform(get("/api/leave-balance/getBalance")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk())
	                .andReturn();

	        String responseJson = result.getResponse().getContentAsString();
	        LeaveBalanceDTO responseDTO = new ObjectMapper().readValue(responseJson, LeaveBalanceDTO.class);

	        assertEquals(LeaveType.VACATION.getDefaultLeaves(), responseDTO.getBalance());
	    }

	    @Test
	    void testUpdateLeaveBalance_positive() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	        request.setLeaveType(LeaveType.VACATION);
	        request.setBalance(5.0);

	        LeaveBalanceDTO updatedBalance = new LeaveBalanceDTO();
	        updatedBalance.setEmployeeId(1001L);
	        updatedBalance.setLeaveType(LeaveType.VACATION);
	        updatedBalance.setBalance(5.0);

	        when(leaveBalanceService.updateLeaveBalance(any(), any(), any())).thenReturn(updatedBalance);

	        mockMvc.perform(put("/api/leave-balance/update")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk());
	    }
	    
	    @Test
	    void testCreateLeaveBalance_positive() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	        request.setLeaveType(LeaveType.SICK);
	        request.setBalance(8.0);

	        LeaveBalanceDTO createdBalance = new LeaveBalanceDTO();
	        createdBalance.setEmployeeId(1001L);
	        createdBalance.setLeaveType(LeaveType.SICK);
	        createdBalance.setBalance(8.0);

	        when(leaveBalanceService.createLeaveBalance(any(), any(), any())).thenReturn(createdBalance);

	        mockMvc.perform(post("/api/leave-balance/create")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk());
	    }
	    
	    @Test
	    void testGetAllLeaveBalancesForEmployee_positive() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);

	        LeaveBalanceDTO leaveBalance1 = new LeaveBalanceDTO();
	        leaveBalance1.setEmployeeId(1001L);
	        leaveBalance1.setLeaveType(LeaveType.SICK);
	        leaveBalance1.setBalance(5.0);

	        LeaveBalanceDTO leaveBalance2 = new LeaveBalanceDTO();
	        leaveBalance2.setEmployeeId(1001L);
	        leaveBalance2.setLeaveType(LeaveType.VACATION);
	        leaveBalance2.setBalance(10.0);

	        List<LeaveBalanceDTO> leaveBalances = Arrays.asList(leaveBalance1, leaveBalance2);

	        when(leaveBalanceService.getAllLeaveBalanceOfEmployee(any())).thenReturn(leaveBalances);

	        mockMvc.perform(get("/api/leave-balance/all")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk());
	    }
	    
	    @Test
	    void testCheckIfBalanceIsSufficient_positive() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	        request.setLeaveType(LeaveType.SICK);
	        request.setBalance(3.0);

	        when(leaveBalanceService.hasSufficientBalance(any(), any(), any())).thenReturn(true);

	        mockMvc.perform(get("/api/leave-balance/sufficient")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk());
	    }
	    
	    
	    @Test
	    void testCheckIfBalanceIsSufficient_negative() throws Exception
	    {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	        request.setLeaveType(LeaveType.SICK);
	        request.setBalance(3.0);
	        when(leaveBalanceService.hasSufficientBalance(any(), any(), any())).thenReturn(false);
	         MvcResult result= mockMvc.perform(get("/api/leave-balance/sufficient")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk())
	                .andReturn();
	         assertEquals("Your balance is not sufficient for the leave",result.getResponse().getContentAsString());
	         }
	    
	    @Test
	    void testDeleteAllLeaveBalancesForEmployee_positive() throws Exception
	    {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);
	    	when(leaveBalanceService.deleteAllLeaveBalancesForEmployee(any())).thenReturn( true);
	    	mockMvc.perform(delete("/api/leave-balance/deleteAll").contentType(MediaType.APPLICATION_JSON)
	                .content(asJsonString(request)))
	                .andExpect(status().isOk());
	    	
	    }
	    
	     
	    

	    @Test
	    void testDeleteAllLeaveBalancesForEmployee_negative() throws Exception {
	        LeaveModuleDTO request = new LeaveModuleDTO();
	        request.setEmployeeId(1001L);

	        doThrow(new ResourceNotFoundException("No leave balances found for Employee ID:1001"))
	                .when(leaveBalanceService).deleteAllLeaveBalancesForEmployee(1001L);

	        mockMvc.perform(delete("/api/leave-balance/deleteAll")
	                        .contentType(MediaType.APPLICATION_JSON)
	                        .content(asJsonString(request)))
	                .andExpect(status().isNotFound());
	    }



}

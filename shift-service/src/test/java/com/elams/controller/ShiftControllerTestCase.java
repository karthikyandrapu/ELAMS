package com.elams.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import com.elams.ShiftServiceApplication;
import com.elams.dtos.ShiftDTO;
import com.elams.entities.Shift;
import com.elams.enums.EmployeeRole;
import com.elams.service.ShiftService;
@ContextConfiguration(classes = {ShiftServiceApplication.class})
class ShiftControllerTestCase {
	
	 @Mock
	 private ShiftService shiftService;
	
	@InjectMocks
	private ShiftControllerImpl shiftController; 

	 @BeforeEach
	    void setup() {
	        MockitoAnnotations.openMocks(this);
	    }

	 @Test
	    @DisplayName("Assign Shift - Positive Test - Manager Role")
	    void assignShift_Positive_ManagerRole() {
	        Long managerId = 1L;
	        ShiftDTO shiftDTO = new ShiftDTO();
	        shiftDTO.setEmployeeId(2L);
	        shiftDTO.setShiftDate(LocalDate.now());
	        shiftDTO.setShiftTime(LocalTime.of(9, 0));

	        EmployeeRole role = EmployeeRole.MANAGER;
	        ShiftDTO assignedShiftDTO = new ShiftDTO();
	        assignedShiftDTO.setShiftId(10L);
	        assignedShiftDTO.setEmployeeId(2L);
	        assignedShiftDTO.setShiftDate(LocalDate.now());
	        assignedShiftDTO.setShiftTime(LocalTime.of(9, 0));

	        when(shiftService.assignShift(any(ShiftDTO.class), eq(managerId))).thenReturn(assignedShiftDTO); 

	        ResponseEntity<ShiftDTO> response = shiftController.assignShift(managerId, shiftDTO, role); 

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(assignedShiftDTO, response.getBody());
	        verify(shiftService).assignShift(any(ShiftDTO.class), eq(managerId));
	    }

	    @Test
	    @DisplayName("Assign Shift - Negative Test - Non-Manager Role")
	    void assignShift_Negative_NonManagerRole() {
	        Long managerId = 1L;
	        ShiftDTO shiftDTO = new ShiftDTO(); 
	        EmployeeRole role = EmployeeRole.EMPLOYEE; 

	        ResponseEntity<ShiftDTO> response = shiftController.assignShift(managerId, shiftDTO, role); 

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, never()).assignShift(any(), anyLong());
	    }
	    @Test
	    @DisplayName("Get Employee Shifts - Positive Test - Shifts Found")
	    void getEmployeeShifts_Positive_ShiftsFound() {
	        
	        Long employeeId = 2L;

	        ShiftDTO shift1 = new ShiftDTO();
	        shift1.setShiftId(10L);
	        shift1.setEmployeeId(employeeId);
	        shift1.setShiftDate(LocalDate.now());
	        shift1.setShiftTime(LocalTime.of(9, 0));

	        ShiftDTO shift2 = new ShiftDTO();
	        shift2.setShiftId(20L);
	        shift2.setEmployeeId(employeeId);
	        shift2.setShiftDate(LocalDate.now().plusDays(1));
	        shift2.setShiftTime(LocalTime.of(14, 0));

	        List<ShiftDTO> shifts = List.of(shift1, shift2);

	        when(shiftService.viewEmployeeShifts(employeeId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getEmployeeShifts(employeeId);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("Get Employee Shifts - Positive Test - No Shifts Found")
	    void getEmployeeShifts_Positive_NoShiftsFound() {
	        Long employeeId = 2L;

	        List<ShiftDTO> shifts = new ArrayList<>();

	        when(shiftService.viewEmployeeShifts(employeeId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getEmployeeShifts(employeeId);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }
	    
	    
	    @Test
	    @DisplayName("View Manager Shifts - Positive Test - Manager Role - Shifts Found")
	    void viewManagerShifts_Positive_ManagerRole_ShiftsFound() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ShiftDTO shift1 = new ShiftDTO();
	        shift1.setShiftId(10L);
	        shift1.setEmployeeId(2L);
	        shift1.setShiftDate(LocalDate.now());
	        shift1.setShiftTime(LocalTime.of(9, 0));

	        ShiftDTO shift2 = new ShiftDTO();
	        shift2.setShiftId(20L);
	        shift2.setEmployeeId(3L);
	        shift2.setShiftDate(LocalDate.now().plusDays(1));
	        shift2.setShiftTime(LocalTime.of(14, 0));

	        List<ShiftDTO> shifts = List.of(shift1, shift2);

	        when(shiftService.viewManagerShifts(managerId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerShifts(managerId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("View Manager Shifts - Positive Test - Manager Role - No Shifts Found")
	    void viewManagerShifts_Positive_ManagerRole_NoShiftsFound() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        List<ShiftDTO> shifts = new ArrayList<>();

	        when(shiftService.viewManagerShifts(managerId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerShifts(managerId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("View Manager Shifts - Negative Test - Non-Manager Role")
	    void viewManagerShifts_Negative_NonManagerRole() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE; 

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerShifts(managerId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    @Test
	    @DisplayName("View Manager Own Shifts - Positive Test - Manager Role - Shifts Found")
	    void viewManagerOwnShifts_Positive_ManagerRole_ShiftsFound() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ShiftDTO shift1 = new ShiftDTO();
	        shift1.setShiftId(10L);
	        shift1.setEmployeeId(managerId); 
	        shift1.setShiftDate(LocalDate.now());
	        shift1.setShiftTime(LocalTime.of(9, 0));

	        ShiftDTO shift2 = new ShiftDTO();
	        shift2.setShiftId(20L);
	        shift2.setEmployeeId(managerId); 
	        shift2.setShiftDate(LocalDate.now().plusDays(1));
	        shift2.setShiftTime(LocalTime.of(14, 0));

	        List<ShiftDTO> shifts = List.of(shift1, shift2);

	        when(shiftService.viewManagerOwnShifts(managerId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerOwnShifts(managerId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("View Manager Own Shifts - Positive Test - Manager Role - No Shifts Found")
	    void viewManagerOwnShifts_Positive_ManagerRole_NoShiftsFound() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        List<ShiftDTO> shifts = new ArrayList<>();

	        when(shiftService.viewManagerOwnShifts(managerId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerOwnShifts(managerId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("View Manager Own Shifts - Negative Test - Non-Manager Role")
	    void viewManagerOwnShifts_Negative_NonManagerRole() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerOwnShifts(managerId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    @Test
	    @DisplayName("Request Shift Swap - Positive Test - Employee Role")
	    void requestShiftSwap_Positive_EmployeeRole() {
	        Long employeeId = 2L;
	        Long shiftId = 10L;
	        Long swapWithEmployeeId = 3L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        ShiftDTO requestedShiftDTO = new ShiftDTO();
	        requestedShiftDTO.setShiftId(shiftId);
	        requestedShiftDTO.setEmployeeId(employeeId);
	        requestedShiftDTO.setShiftDate(LocalDate.now());
	        requestedShiftDTO.setShiftTime(LocalTime.of(9, 0));

	        when(shiftService.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId)).thenReturn(requestedShiftDTO);

	        ResponseEntity<ShiftDTO> response = shiftController.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(requestedShiftDTO, response.getBody());
	    }

	    @Test
	    @DisplayName("Request Shift Swap - Negative Test - Non-Employee Role")
	    void requestShiftSwap_Negative_NonEmployeeRole() {
	        Long employeeId = 2L;
	        Long shiftId = 10L;
	        Long swapWithEmployeeId = 3L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<ShiftDTO> response = shiftController.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    
	    @Test
	    @DisplayName("Approve Shift Swap - Positive Test - Manager Role")
	    void approveShiftSwap_Positive_ManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        Long swapWithEmployeeId = 3L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ShiftDTO approvedShiftDTO = new ShiftDTO();
	        approvedShiftDTO.setShiftId(shiftId);
	        approvedShiftDTO.setEmployeeId(swapWithEmployeeId);
	        approvedShiftDTO.setShiftDate(LocalDate.now());
	        approvedShiftDTO.setShiftTime(LocalTime.of(9, 0));

	        when(shiftService.approveShiftSwap(managerId, shiftId, swapWithEmployeeId)).thenReturn(approvedShiftDTO);

	        ResponseEntity<ShiftDTO> response = shiftController.approveShiftSwap(shiftId, managerId, swapWithEmployeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(approvedShiftDTO, response.getBody());
	    }

	    @Test
	    @DisplayName("Approve Shift Swap - Negative Test - Non-Manager Role")
	    void approveShiftSwap_Negative_NonManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        Long swapWithEmployeeId = 3L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE; 

	        ResponseEntity<ShiftDTO> response = shiftController.approveShiftSwap(shiftId, managerId, swapWithEmployeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    
	    @Test
	    @DisplayName("Reject Shift Swap - Positive Test - Manager Role")
	    void rejectShiftSwap_Positive_ManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ShiftDTO rejectedShiftDTO = new ShiftDTO();
	        rejectedShiftDTO.setShiftId(shiftId);
	        rejectedShiftDTO.setShiftDate(LocalDate.now());
	        rejectedShiftDTO.setShiftTime(LocalTime.of(9, 0));

	        when(shiftService.rejectShiftSwap(managerId, shiftId)).thenReturn(rejectedShiftDTO);

	        ResponseEntity<ShiftDTO> response = shiftController.rejectShiftSwap(shiftId, managerId, role);

	        
	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(rejectedShiftDTO, response.getBody());
	    }

	    @Test
	    @DisplayName("Reject Shift Swap - Negative Test - Non-Manager Role")
	    void rejectShiftSwap_Negative_NonManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        ResponseEntity<ShiftDTO> response = shiftController.rejectShiftSwap(shiftId, managerId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    @Test
	    @DisplayName("Update Shift - Positive Test - Manager Role")
	    void updateShift_Positive_ManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        Shift inputShiftDTO = new Shift();
	        inputShiftDTO.setShiftDate(LocalDate.now().plusDays(1));
	        inputShiftDTO.setShiftTime(LocalTime.of(10, 0));

	        ShiftDTO updatedShiftDTO = new ShiftDTO();
	        updatedShiftDTO.setShiftId(shiftId);
	        updatedShiftDTO.setShiftDate(inputShiftDTO.getShiftDate());
	        updatedShiftDTO.setShiftTime(inputShiftDTO.getShiftTime());

	        when(shiftService.updateShift(managerId, shiftId, inputShiftDTO.getShiftDate(), inputShiftDTO.getShiftTime())).thenReturn(updatedShiftDTO);

	        ResponseEntity<ShiftDTO> response = shiftController.updateShift(shiftId, managerId, inputShiftDTO, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(updatedShiftDTO, response.getBody());
	    }

	    @Test
	    @DisplayName("Update Shift - Negative Test - Non-Manager Role")
	    void updateShift_Negative_NonManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        Shift inputShiftDTO = new Shift();
	        inputShiftDTO.setShiftDate(LocalDate.now().plusDays(1));
	        inputShiftDTO.setShiftTime(LocalTime.of(10, 0));

	        ResponseEntity<ShiftDTO> response = shiftController.updateShift(shiftId, managerId, inputShiftDTO, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	
	    @Test
	    @DisplayName("Delete Shift - Positive Test - Manager Role")
	    void deleteShift_Positive_ManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<Void> response = shiftController.deleteShift(shiftId, managerId, role);

	        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	        verify(shiftService).deleteShift(managerId, shiftId);
	    }

	    @Test
	    @DisplayName("Delete Shift - Negative Test - Non-Manager Role")
	    void deleteShift_Negative_NonManagerRole() {
	        Long shiftId = 10L;
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        ResponseEntity<Void> response = shiftController.deleteShift(shiftId, managerId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, org.mockito.Mockito.never()).deleteShift(managerId, shiftId);
	    }
	    @Test
	    @DisplayName("Get Colleague Shifts - Positive Test - Shifts Found")
	    void getColleagueShifts_Positive_ShiftsFound() {
	        Long employeeId = 2L;
	        LocalDate shiftDate = LocalDate.now();
	        EmployeeRole role = EmployeeRole.EMPLOYEE; 

	        ShiftDTO shift1 = new ShiftDTO();
	        shift1.setShiftId(10L);
	        shift1.setEmployeeId(3L);
	        shift1.setShiftDate(shiftDate);
	        shift1.setShiftTime(LocalTime.of(9, 0));

	        ShiftDTO shift2 = new ShiftDTO();
	        shift2.setShiftId(20L);
	        shift2.setEmployeeId(4L);
	        shift2.setShiftDate(shiftDate);
	        shift2.setShiftTime(LocalTime.of(14, 0));

	        List<ShiftDTO> colleagueShifts = List.of(shift1, shift2);

	        when(shiftService.getColleagueShifts(employeeId, shiftDate)).thenReturn(colleagueShifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getColleagueShifts(employeeId, shiftDate, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(colleagueShifts, response.getBody());
	    }

	    @Test
	    @DisplayName("Get Colleague Shifts - Positive Test - No Shifts Found")
	    void getColleagueShifts_Positive_NoShiftsFound() {
	        Long employeeId = 2L;
	        LocalDate shiftDate = LocalDate.now();
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        List<ShiftDTO> colleagueShifts = new ArrayList<>();

	        when(shiftService.getColleagueShifts(employeeId, shiftDate)).thenReturn(colleagueShifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getColleagueShifts(employeeId, shiftDate, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(colleagueShifts, response.getBody());
	    }
	    @Test
	    @DisplayName("View Manager Employee Shifts - Positive Test - Manager Role - Shifts Found")
	    void viewManagerEmployeeShifts_Positive_ManagerRole_ShiftsFound() {
	        Long managerId = 1L;
	        Long employeeId = 2L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ShiftDTO shift1 = new ShiftDTO();
	        shift1.setShiftId(10L);
	        shift1.setEmployeeId(employeeId);
	        shift1.setShiftDate(LocalDate.now());
	        shift1.setShiftTime(LocalTime.of(9, 0));

	        ShiftDTO shift2 = new ShiftDTO();
	        shift2.setShiftId(20L);
	        shift2.setEmployeeId(employeeId);
	        shift2.setShiftDate(LocalDate.now().plusDays(1));
	        shift2.setShiftTime(LocalTime.of(14, 0));

	        List<ShiftDTO> shifts = List.of(shift1, shift2);

	        when(shiftService.viewManagerEmployeeShifts(managerId, employeeId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerEmployeeShifts(managerId, employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("View Manager Employee Shifts - Positive Test - Manager Role - No Shifts Found")
	    void viewManagerEmployeeShifts_Positive_ManagerRole_NoShiftsFound() {
	        Long managerId = 1L;
	        Long employeeId = 2L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        List<ShiftDTO> shifts = new ArrayList<>();

	        when(shiftService.viewManagerEmployeeShifts(managerId, employeeId)).thenReturn(shifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerEmployeeShifts(managerId, employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(shifts, response.getBody());
	    }

	    @Test
	    @DisplayName("View Manager Employee Shifts - Negative Test - Non-Manager Role")
	    void viewManagerEmployeeShifts_Negative_NonManagerRole() {
	        Long managerId = 1L;
	        Long employeeId = 2L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewManagerEmployeeShifts(managerId, employeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    @Test
	    @DisplayName("Get Manager Swap Requests - Positive Test - Manager Role - Requests Found")
	    void getManagerSwapRequests_Positive_ManagerRole_RequestsFound() {
	        
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ShiftDTO request1 = new ShiftDTO();
	        request1.setShiftId(10L);
	        request1.setEmployeeId(2L);
	        request1.setShiftDate(LocalDate.now());
	        request1.setShiftTime(LocalTime.of(9, 0));

	        ShiftDTO request2 = new ShiftDTO();
	        request2.setShiftId(20L);
	        request2.setEmployeeId(3L);
	        request2.setShiftDate(LocalDate.now().plusDays(1));
	        request2.setShiftTime(LocalTime.of(14, 0));

	        List<ShiftDTO> requests = List.of(request1, request2);

	        when(shiftService.getManagerSwapRequests(managerId)).thenReturn(requests);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getManagerSwapRequests(managerId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(requests, response.getBody());
	    }
	    
	    @Test
	    @DisplayName("Get Manager Swap Requests - Positive Test - Manager Role - No Requests Found")
	    void getManagerSwapRequests_Positive_ManagerRole_NoRequestsFound() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        List<ShiftDTO> requests = new ArrayList<>();

	        when(shiftService.getManagerSwapRequests(managerId)).thenReturn(requests);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getManagerSwapRequests(managerId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(requests, response.getBody());
	    }

	    @Test
	    @DisplayName("Get Manager Swap Requests - Negative Test - Non-Manager Role")
	    void getManagerSwapRequests_Negative_NonManagerRole() {
	        Long managerId = 1L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getManagerSwapRequests(managerId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	    }
	    
	    @Test
	    @DisplayName("Get Upcoming Employee Shifts - Positive Test - Employee Role")
	    void getUpcomingEmployeeShifts_Positive_EmployeeRole() {
	    	Long employeeId = 2L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;
	        List<ShiftDTO> upcomingShifts = List.of(new ShiftDTO(), new ShiftDTO()); // Mock upcoming shifts

	        when(shiftService.viewUpcomingEmployeeShifts(employeeId)).thenReturn(upcomingShifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getUpcomingEmployeeShifts(employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(upcomingShifts, response.getBody());
	        verify(shiftService).viewUpcomingEmployeeShifts(employeeId); }
    
	    @Test
	    @DisplayName("Get Upcoming Employee Shifts - Negative Test - Non-Employee Role")
	    void getUpcomingEmployeeShifts_Negative_NonEmployeeRole() {
	        Long employeeId = 2L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.getUpcomingEmployeeShifts(employeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, never()).viewUpcomingEmployeeShifts(anyLong());
	    }
	    
	    @Test
	    @DisplayName("View Employee Swap Requests - Positive Test - Employee Role")
	    void viewEmployeeSwapRequests_Positive_EmployeeRole() {
	        Long employeeId = 3L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;
	        List<ShiftDTO> swapRequests = List.of(new ShiftDTO()); // Mock employee's swap requests

	        when(shiftService.viewEmployeeSwapRequests(employeeId)).thenReturn(swapRequests);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeSwapRequests(employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(swapRequests, response.getBody());
	        verify(shiftService).viewEmployeeSwapRequests(employeeId);
	    }
	    
	    @Test
	    @DisplayName("View Employee Swap Requests - Negative Test - Non-Employee Role")
	    void viewEmployeeSwapRequests_Negative_NonEmployeeRole() {
	        Long employeeId = 3L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeSwapRequests(employeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, never()).viewEmployeeSwapRequests(anyLong());
	    }
	    
	    
	    @Test
	    @DisplayName("View Employee Rejected Swap Requests - Positive Test - Employee Role")
	    void viewEmployeeRejectedSwapRequests_Positive_EmployeeRole() {
	        Long employeeId = 4L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;
	        List<ShiftDTO> rejectedRequests = List.of(new ShiftDTO()); // Mock rejected swap requests

	        when(shiftService.viewEmployeeRejectedSwapRequests(employeeId)).thenReturn(rejectedRequests);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeRejectedSwapRequests(employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(rejectedRequests, response.getBody());
	        verify(shiftService).viewEmployeeRejectedSwapRequests(employeeId);
	    }
	    
	    @Test
	    @DisplayName("View Employee Rejected Swap Requests - Negative Test - Non-Employee Role")
	    void viewEmployeeRejectedSwapRequests_Negative_NonEmployeeRole() {
	        Long employeeId = 4L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeRejectedSwapRequests(employeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, never()).viewEmployeeRejectedSwapRequests(anyLong());
	    }
	    
	    
	    @Test
	    @DisplayName("View Employee Approved Swap Requests - Positive Test - Employee Role")
	    void viewEmployeeApprovedSwapRequests_Positive_EmployeeRole() {
	        Long employeeId = 5L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;
	        List<ShiftDTO> approvedRequests = List.of(new ShiftDTO()); // Mock approved swap requests

	        when(shiftService.viewEmployeeApprovedSwapRequests(employeeId)).thenReturn(approvedRequests);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeApprovedSwapRequests(employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(approvedRequests, response.getBody());
	        verify(shiftService).viewEmployeeApprovedSwapRequests(employeeId);
	    }
	    
	    
	    @Test
	    @DisplayName("View Employee Approved Swap Requests - Negative Test - Non-Employee Role")
	    void viewEmployeeApprovedSwapRequests_Negative_NonEmployeeRole() {
	        Long employeeId = 5L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeApprovedSwapRequests(employeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, never()).viewEmployeeApprovedSwapRequests(anyLong());
	    }

	    @Test
	    @DisplayName("View Employee Swapped With Another Employee - Positive Test - Employee Role")
	    void viewEmployeeSwappedWithAnotherEmployee_Positive_EmployeeRole() {
	        Long employeeId = 6L;
	        EmployeeRole role = EmployeeRole.EMPLOYEE;
	        List<ShiftDTO> swappedShifts = List.of(new ShiftDTO()); // Mock swapped shifts

	        when(shiftService.viewEmployeeSwappedWithAnotherEmployee(employeeId)).thenReturn(swappedShifts);

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeSwappedWithAnotherEmployee(employeeId, role);

	        assertEquals(HttpStatus.OK, response.getStatusCode());
	        assertEquals(swappedShifts, response.getBody());
	        verify(shiftService).viewEmployeeSwappedWithAnotherEmployee(employeeId);
	    }
	    
	    @Test
	    @DisplayName("View Employee Swapped With Another Employee - Negative Test - Non-Employee Role")
	    void viewEmployeeSwappedWithAnotherEmployee_Negative_NonEmployeeRole() {
	        Long employeeId = 6L;
	        EmployeeRole role = EmployeeRole.MANAGER;

	        ResponseEntity<List<ShiftDTO>> response = shiftController.viewEmployeeSwappedWithAnotherEmployee(employeeId, role);

	        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
	        verify(shiftService, never()).viewEmployeeSwappedWithAnotherEmployee(anyLong());
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
}

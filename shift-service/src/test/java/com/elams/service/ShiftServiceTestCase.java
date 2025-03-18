package com.elams.service;

import com.elams.ShiftServiceApplication;
import com.elams.dtos.EmployeeDTO;
import com.elams.dtos.ShiftDTO;
import com.elams.dtos.ShiftStatusDTO;
import com.elams.enums.ShiftStatusType;
import com.elams.entities.Shift;
import com.elams.entities.ShiftStatus;
import com.elams.exceptions.*;
import com.elams.feign.EmployeeServiceClient;
import com.elams.mapper.ShiftMapper;
import com.elams.mapper.ShiftStatusMapper;
import com.elams.repository.ShiftRepository;
import com.elams.repository.ShiftStatusRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ShiftServiceApplication.class})
@DisplayName("Shift Service Tests")
class ShiftServiceTestCase {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftStatusRepository shiftStatusRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ShiftMapper shiftMapper;

    @Mock
    private ShiftStatusMapper shiftStatusMapper;

    @Mock
    private EmployeeServiceClient employeeClient;

    @InjectMocks
    private ShiftServiceImpl shiftServiceImpl;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(shiftServiceImpl, "shiftDurationHours", 60);
    }

    @AfterEach
    void tearDown() {
        shiftRepository = null;
        shiftStatusRepository = null;
        modelMapper = null;
        shiftMapper = null;
        shiftStatusMapper = null;
        employeeClient = null;
        shiftServiceImpl = null;
    }

    @Test
    @DisplayName("Assign Shift - Positive Test - Valid Assignment")
    void assignShift_Positive_ValidAssignment() {
        Long managerId = 1L;
        Long employeeId = 2L;
        ShiftDTO shiftDTO = new ShiftDTO(); 
        shiftDTO.setEmployeeId(employeeId);
        shiftDTO.setShiftDate(LocalDate.now());
        shiftDTO.setShiftTime(LocalTime.of(9, 0));
        shiftDTO.setShiftId(3L);

        Shift shift = new Shift(); 
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));
        shift.setShiftId(3L);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setManagerId(managerId);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shift.getShiftId());

        ShiftDTO expectedShiftDTO = new ShiftDTO();
        expectedShiftDTO.setEmployeeId(employeeId);
        expectedShiftDTO.setShiftDate(LocalDate.now());
        expectedShiftDTO.setShiftTime(LocalTime.of(9, 0));
        expectedShiftDTO.setShiftId(3L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employeeDTO);
        when(shiftRepository.existsByEmployeeIdAndShiftDate(employeeId, shiftDTO.getShiftDate())).thenReturn(false);
        when(shiftMapper.toEntity(shiftDTO)).thenReturn(shift);
        when(shiftRepository.save(any(Shift.class))).thenReturn(shift);
        when(shiftStatusRepository.save(any(ShiftStatus.class))).thenReturn(shiftStatus);
        when(shiftMapper.toDTO(shift)).thenReturn(expectedShiftDTO);
        when(shiftStatusMapper.toDTO(any(ShiftStatus.class))).thenReturn(new ShiftStatusDTO());

        ShiftDTO result = shiftServiceImpl.assignShift(shiftDTO, managerId);

        assertNotNull(result);
        assertEquals(expectedShiftDTO, result);
        verify(shiftRepository, times(1)).save(any(Shift.class));
        verify(shiftStatusRepository, times(1)).save(any(ShiftStatus.class));
    }

    @Test
    @DisplayName("Assign Shift - Negative Test - Unauthorized Manager")
    void assignShift_Negative_UnauthorizedManager() {
        Long managerId = 1L;
        Long employeeId = 2L;
        ShiftDTO shiftDTO = new ShiftDTO(); 
        shiftDTO.setEmployeeId(employeeId);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setManagerId(3L); 

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employeeDTO);

        assertThrows(UnauthorizedManagerException.class, () -> shiftServiceImpl.assignShift(shiftDTO, managerId));
        verify(shiftRepository, never()).save(any(Shift.class));
        verify(shiftStatusRepository, never()).save(any(ShiftStatus.class));
    }

    @Test
    @DisplayName("Assign Shift - Negative Test - Shift Conflict")
    void assignShift_Negative_ShiftConflict() {
        Long managerId = 1L;
        Long employeeId = 2L;
        ShiftDTO shiftDTO = new ShiftDTO(); 
        shiftDTO.setEmployeeId(employeeId);
        shiftDTO.setShiftDate(LocalDate.now());

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employeeId);
        employeeDTO.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employeeDTO);
        when(shiftRepository.existsByEmployeeIdAndShiftDate(employeeId, shiftDTO.getShiftDate())).thenReturn(true);

        assertThrows(ShiftConflictException.class, () -> shiftServiceImpl.assignShift(shiftDTO, managerId));
        verify(shiftRepository, never()).save(any(Shift.class));
        verify(shiftStatusRepository, never()).save(any(ShiftStatus.class));
    }

    @Test
    @DisplayName("Assign Shift - Negative Test - Employee Not Found")
    void assignShift_Negative_EmployeeNotFound() {
        Long managerId = 1L;
        Long employeeId = 2L;
        ShiftDTO shiftDTO = new ShiftDTO(); 
        shiftDTO.setEmployeeId(employeeId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class, () -> shiftServiceImpl.assignShift(shiftDTO, managerId));
        verify(shiftRepository, never()).save(any(Shift.class));
        verify(shiftStatusRepository, never()).save(any(ShiftStatus.class));
    }
    @Test
    @DisplayName("Complete Assigned Shifts - Positive Test - Shifts Completed")
    void completeAssignedShifts_Positive_ShiftsCompleted() {
        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setShiftId(1L);
        shiftStatus1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setShiftId(2L);
        shiftStatus2.setStatus(ShiftStatusType.SWAP_REQUEST_APPROVED);

        List<ShiftStatus> shiftsToComplete = List.of(shiftStatus1, shiftStatus2);
        when(shiftStatusRepository.findByStatusIn(anyList())).thenReturn(shiftsToComplete);

        Shift shift1 = new Shift();
        shift1.setShiftId(1L);
        shift1.setShiftDate(LocalDate.now().minusDays(1));
        shift1.setShiftTime(LocalTime.of(10, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(2L);
        shift2.setShiftDate(LocalDate.now().minusDays(1));
        shift2.setShiftTime(LocalTime.of(12, 0));

        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift1));
        when(shiftRepository.findById(2L)).thenReturn(Optional.of(shift2));

        shiftServiceImpl.completeAssignedShifts();

        verify(shiftStatusRepository, times(2)).save(any(ShiftStatus.class));
        verify(shiftStatusRepository, times(1)).findByStatusIn(anyList());
        verify(shiftRepository, times(2)).findById(anyLong());

        assertEquals(ShiftStatusType.COMPLETED, shiftStatus1.getStatus());
        assertEquals(ShiftStatusType.COMPLETED, shiftStatus2.getStatus());
    }

    @Test
    @DisplayName("Complete Assigned Shifts - Negative Test - Shift Not Found")
    void completeAssignedShifts_Negative_ShiftNotFound() {
        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(1L);
        shiftStatus.setStatus(ShiftStatusType.SCHEDULED);

        when(shiftStatusRepository.findByStatusIn(anyList())).thenReturn(List.of(shiftStatus));
        when(shiftRepository.findById(1L)).thenReturn(Optional.empty());

        shiftServiceImpl.completeAssignedShifts();

        verify(shiftStatusRepository, never()).save(any(ShiftStatus.class));
    }

    @Test
    @DisplayName("Complete Assigned Shifts - Negative Test - DateTimeException")
    void completeAssignedShifts_Negative_DateTimeException() {
        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(1L);
        shiftStatus.setStatus(ShiftStatusType.SCHEDULED);

        Shift shift = new Shift();
        shift.setShiftId(1L);
        shift.setShiftDate(LocalDate.now().minusDays(1));
        shift.setShiftTime(null); 

        when(shiftStatusRepository.findByStatusIn(anyList())).thenReturn(List.of(shiftStatus));
        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));

        shiftServiceImpl.completeAssignedShifts();
        verify(shiftStatusRepository, never()).save(any(ShiftStatus.class));
    }

    @Test
    @DisplayName("Complete Assigned Shifts - Positive Test - No Shifts to Complete")
    void completeAssignedShifts_Positive_NoShiftsToComplete() {
        when(shiftStatusRepository.findByStatusIn(anyList())).thenReturn(new ArrayList<>());
        shiftServiceImpl.completeAssignedShifts();

        verify(shiftStatusRepository, never()).save(any(ShiftStatus.class));
        verify(shiftRepository, never()).findById(anyLong());
    }
    @Test
    @DisplayName("View Employee Shifts - Positive Test - Shifts Found")
    void viewEmployeeShifts_Positive_ShiftsFound() {
        Long employeeId = 1L;
        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(employeeId);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(20L);
        shift2.setEmployeeId(employeeId);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(14, 0));

        List<Shift> shifts = List.of(shift1, shift2);
        when(shiftRepository.findByEmployeeId(employeeId)).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(employeeId);
        dto1.setShiftDate(LocalDate.now());
        dto1.setShiftTime(LocalTime.of(9, 0));

        ShiftDTO dto2 = new ShiftDTO();
        dto2.setShiftId(20L);
        dto2.setEmployeeId(employeeId);
        dto2.setShiftDate(LocalDate.now().plusDays(1));
        dto2.setShiftTime(LocalTime.of(14, 0));

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);
        when(shiftMapper.toDTO(shift2)).thenReturn(dto2);

        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setShiftId(10L);
        shiftStatus1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setShiftId(20L);
        shiftStatus2.setStatus(ShiftStatusType.COMPLETED);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(shiftStatus1);
        when(shiftStatusRepository.findByShiftId(20L)).thenReturn(shiftStatus2);

        ShiftStatusDTO statusDTO1 = new ShiftStatusDTO();
        statusDTO1.setShiftId(10L);
        statusDTO1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatusDTO statusDTO2 = new ShiftStatusDTO();
        statusDTO2.setShiftId(20L);
        statusDTO2.setStatus(ShiftStatusType.COMPLETED);

        when(shiftStatusMapper.toDTO(shiftStatus1)).thenReturn(statusDTO1);
        when(shiftStatusMapper.toDTO(shiftStatus2)).thenReturn(statusDTO2);

        List<ShiftDTO> result = shiftServiceImpl.viewEmployeeShifts(employeeId);

        assertEquals(2, result.size());
        assertEquals(statusDTO1, result.get(0).getShiftStatus());
        assertEquals(statusDTO2, result.get(1).getShiftStatus());
    }

    @Test
    @DisplayName("View Employee Shifts - Positive Test - Shifts Found, Status Null")
    void viewEmployeeShifts_Positive_ShiftsFound_StatusNull() {
        Long employeeId = 1L;
        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(employeeId);

        List<Shift> shifts = List.of(shift1);
        when(shiftRepository.findByEmployeeId(employeeId)).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(employeeId);

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(null);

        List<ShiftDTO> result = shiftServiceImpl.viewEmployeeShifts(employeeId);

        assertEquals(1, result.size());
        assertEquals(ShiftStatusType.OPEN, result.get(0).getShiftStatus().getStatus());
    }

    @Test
    @DisplayName("View Employee Shifts - Negative Test - Shifts Not Found")
    void viewEmployeeShifts_Negative_ShiftsNotFound() {
        Long employeeId = 1L;
        when(shiftRepository.findByEmployeeId(employeeId)).thenReturn(new ArrayList<>());

        assertThrows(EmployeeShiftsNotFoundException.class, () -> shiftServiceImpl.viewEmployeeShifts(employeeId));
    }
    
    
    @Test
    @DisplayName("View Manager Shifts - Positive Test - Shifts Found")
    void viewManagerShifts_Positive_ShiftsFound() {
        Long managerId = 1L;
        EmployeeDTO subordinate1 = new EmployeeDTO();
        subordinate1.setId(2L);
        subordinate1.setManagerId(managerId);

        EmployeeDTO subordinate2 = new EmployeeDTO();
        subordinate2.setId(3L);
        subordinate2.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate1, subordinate2);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(2L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(20L);
        shift2.setEmployeeId(3L);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(14, 0));

        List<Shift> shifts = List.of(shift1, shift2);
        when(shiftRepository.findByEmployeeIdIn(List.of(2L, 3L))).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(2L);
        dto1.setShiftDate(LocalDate.now());
        dto1.setShiftTime(LocalTime.of(9, 0));

        ShiftDTO dto2 = new ShiftDTO();
        dto2.setShiftId(20L);
        dto2.setEmployeeId(3L);
        dto2.setShiftDate(LocalDate.now().plusDays(1));
        dto2.setShiftTime(LocalTime.of(14, 0));

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);
        when(shiftMapper.toDTO(shift2)).thenReturn(dto2);

        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setShiftId(10L);
        shiftStatus1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setShiftId(20L);
        shiftStatus2.setStatus(ShiftStatusType.COMPLETED);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(shiftStatus1);
        when(shiftStatusRepository.findByShiftId(20L)).thenReturn(shiftStatus2);

        ShiftStatusDTO statusDTO1 = new ShiftStatusDTO();
        statusDTO1.setShiftId(10L);
        statusDTO1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatusDTO statusDTO2 = new ShiftStatusDTO();
        statusDTO2.setShiftId(20L);
        statusDTO2.setStatus(ShiftStatusType.COMPLETED);

        when(shiftStatusMapper.toDTO(shiftStatus1)).thenReturn(statusDTO1);
        when(shiftStatusMapper.toDTO(shiftStatus2)).thenReturn(statusDTO2);

        List<ShiftDTO> result = shiftServiceImpl.viewManagerShifts(managerId);

        assertEquals(2, result.size());
        assertEquals(statusDTO1, result.get(0).getShiftStatus());
        assertEquals(statusDTO2, result.get(1).getShiftStatus());
    }

    @Test
    @DisplayName("View Manager Shifts - Negative Test - Subordinates Not Found")
    void viewManagerShifts_Negative_SubordinatesNotFound() {
        Long managerId = 1L;
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(new ArrayList<>());

        assertThrows(ManagerSubordinatesNotFoundException.class, () -> shiftServiceImpl.viewManagerShifts(managerId));
        verify(shiftRepository, never()).findByEmployeeIdIn(anyList());
    }

    @Test
    @DisplayName("View Manager Shifts - Positive Test - Shifts Found, Status Null")
    void viewManagerShifts_Positive_ShiftsFound_StatusNull() {
        Long managerId = 1L;
        EmployeeDTO subordinate1 = new EmployeeDTO();
        subordinate1.setId(2L);
        subordinate1.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate1);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(2L);

        List<Shift> shifts = List.of(shift1);
        when(shiftRepository.findByEmployeeIdIn(List.of(2L))).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(2L);

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(null);

        List<ShiftDTO> result = shiftServiceImpl.viewManagerShifts(managerId);

        assertEquals(1, result.size());
        assertEquals(ShiftStatusType.OPEN, result.get(0).getShiftStatus().getStatus());
    }
    @Test
    @DisplayName("View Manager Own Shifts - Positive Test - Shifts Found")
    void viewManagerOwnShifts_Positive_ShiftsFound() {
        Long managerId = 1L;
        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(managerId);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(20L);
        shift2.setEmployeeId(managerId);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(14, 0));

        List<Shift> shifts = List.of(shift1, shift2);
        when(shiftRepository.findByEmployeeId(managerId)).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(managerId);
        dto1.setShiftDate(LocalDate.now());
        dto1.setShiftTime(LocalTime.of(9, 0));

        ShiftDTO dto2 = new ShiftDTO();
        dto2.setShiftId(20L);
        dto2.setEmployeeId(managerId);
        dto2.setShiftDate(LocalDate.now().plusDays(1));
        dto2.setShiftTime(LocalTime.of(14, 0));

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);
        when(shiftMapper.toDTO(shift2)).thenReturn(dto2);

        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setShiftId(10L);
        shiftStatus1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setShiftId(20L);
        shiftStatus2.setStatus(ShiftStatusType.COMPLETED);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(shiftStatus1);
        when(shiftStatusRepository.findByShiftId(20L)).thenReturn(shiftStatus2);

        ShiftStatusDTO statusDTO1 = new ShiftStatusDTO();
        statusDTO1.setShiftId(10L);
        statusDTO1.setStatus(ShiftStatusType.SCHEDULED);

        ShiftStatusDTO statusDTO2 = new ShiftStatusDTO();
        statusDTO2.setShiftId(20L);
        statusDTO2.setStatus(ShiftStatusType.COMPLETED);

        when(shiftStatusMapper.toDTO(shiftStatus1)).thenReturn(statusDTO1);
        when(shiftStatusMapper.toDTO(shiftStatus2)).thenReturn(statusDTO2);

        List<ShiftDTO> result = shiftServiceImpl.viewManagerOwnShifts(managerId);

        assertEquals(2, result.size());
        assertEquals(statusDTO1, result.get(0).getShiftStatus());
        assertEquals(statusDTO2, result.get(1).getShiftStatus());
    }

    @Test
    @DisplayName("View Manager Own Shifts - Negative Test - Shifts Not Found")
    void viewManagerOwnShifts_Negative_ShiftsNotFound() {
        Long managerId = 1L;
        when(shiftRepository.findByEmployeeId(managerId)).thenReturn(new ArrayList<>());

        assertThrows(ManagerOwnShiftsNotFoundException.class, () -> shiftServiceImpl.viewManagerOwnShifts(managerId));
    }

    @Test
    @DisplayName("View Manager Own Shifts - Positive Test - Shifts Found, Status Null")
    void viewManagerOwnShifts_Positive_ShiftsFound_StatusNull() {
        Long managerId = 1L;
        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(managerId);

        List<Shift> shifts = List.of(shift1);
        when(shiftRepository.findByEmployeeId(managerId)).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(managerId);

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(null);

        List<ShiftDTO> result = shiftServiceImpl.viewManagerOwnShifts(managerId);

        assertEquals(1, result.size());
        assertEquals(ShiftStatusType.OPEN, result.get(0).getShiftStatus().getStatus());
        assertNull(result.get(0).getShiftStatus().getRequestedSwapEmployeeId());
    }
    @Test
    @DisplayName("Get Colleague Shifts - Positive Test - Shifts Found")
    void getColleagueShifts_Positive_ShiftsFound() {
        Long employeeId = 1L;
        LocalDate shiftDate = LocalDate.now();

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(10L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        EmployeeDTO colleague1 = new EmployeeDTO();
        colleague1.setId(2L);
        colleague1.setManagerId(10L);

        EmployeeDTO colleague2 = new EmployeeDTO();
        colleague2.setId(3L);
        colleague2.setManagerId(10L);

        List<EmployeeDTO> colleagues = List.of(colleague1, colleague2);
        when(employeeClient.getEmployeesByManager(10L)).thenReturn(colleagues);

        Shift shift1 = new Shift();
        shift1.setShiftId(100L);
        shift1.setEmployeeId(2L);
        shift1.setShiftDate(shiftDate);
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(200L);
        shift2.setEmployeeId(3L);
        shift2.setShiftDate(shiftDate);
        shift2.setShiftTime(LocalTime.of(14, 0));

        List<Shift> shifts = List.of(shift1, shift2);
        when(shiftRepository.findByShiftDate(shiftDate)).thenReturn(shifts);

        EmployeeDTO shiftEmployee1 = new EmployeeDTO();
        shiftEmployee1.setId(2L);
        shiftEmployee1.setManagerId(10L);

        EmployeeDTO shiftEmployee2 = new EmployeeDTO();
        shiftEmployee2.setId(3L);
        shiftEmployee2.setManagerId(10L);

        when(employeeClient.getEmployeeById(2L)).thenReturn(shiftEmployee1);
        when(employeeClient.getEmployeeById(3L)).thenReturn(shiftEmployee2);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(100L);
        dto1.setEmployeeId(2L);
        dto1.setShiftDate(shiftDate);
        dto1.setShiftTime(LocalTime.of(9, 0));

        ShiftDTO dto2 = new ShiftDTO();
        dto2.setShiftId(200L);
        dto2.setEmployeeId(3L);
        dto2.setShiftDate(shiftDate);
        dto2.setShiftTime(LocalTime.of(14, 0));

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);
        when(shiftMapper.toDTO(shift2)).thenReturn(dto2);

        List<ShiftDTO> result = shiftServiceImpl.getColleagueShifts(employeeId, shiftDate);

        assertEquals(2, result.size());
        assertEquals(dto1, result.get(0));
        assertEquals(dto2, result.get(1));
    }

    @Test
    @DisplayName("Get Colleague Shifts - Negative Test - Null Shift Date")
    void getColleagueShifts_Negative_NullShiftDate() {
        Long employeeId = 1L;
        LocalDate shiftDate = null;

        assertThrows(IllegalArgumentException.class, () -> shiftServiceImpl.getColleagueShifts(employeeId, shiftDate));
    }

    @Test
    @DisplayName("Get Colleague Shifts - Negative Test - Employee Not Found")
    void getColleagueShifts_Negative_EmployeeNotFound() {
        Long employeeId = 1L;
        LocalDate shiftDate = LocalDate.now();

        when(employeeClient.getEmployeeById(employeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        assertThrows(EmployeeNotFoundException.class, () -> shiftServiceImpl.getColleagueShifts(employeeId, shiftDate));
    }

    @Test
    @DisplayName("Get Colleague Shifts - Negative Test - Manager Not Found")
    void getColleagueShifts_Negative_ManagerNotFound() {
        Long employeeId = 1L;
        LocalDate shiftDate = LocalDate.now();

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(null);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        assertThrows(ManagerNotFoundException.class, () -> shiftServiceImpl.getColleagueShifts(employeeId, shiftDate));
    }

    @Test
    @DisplayName("Get Colleague Shifts - Negative Test - Subordinates Not Found")
    void getColleagueShifts_Negative_SubordinatesNotFound() {
        Long employeeId = 1L;
        LocalDate shiftDate = LocalDate.now();

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(10L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);
        when(employeeClient.getEmployeesByManager(10L)).thenThrow(new ManagerSubordinatesNotFoundException("Subordinates not found"));

        assertThrows(ManagerSubordinatesNotFoundException.class, () -> shiftServiceImpl.getColleagueShifts(employeeId, shiftDate));
    }
    

    @Test
    @DisplayName("Request Shift Swap - Positive Test - Swap Requested")
    void requestShiftSwap_Positive_SwapRequested() {
        
        Long employeeId = 1L;
        Long shiftId = 10L;
        Long swapWithEmployeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(100L);

        EmployeeDTO swapEmployee = new EmployeeDTO();
        swapEmployee.setId(swapWithEmployeeId);
        swapEmployee.setManagerId(100L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);
        when(employeeClient.getEmployeeById(swapWithEmployeeId)).thenReturn(swapEmployee);

        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(swapWithEmployeeId, shift.getShiftDate(), shift.getShiftTime())).thenReturn(false);

        when(shiftRepository.findByEmployeeIdAndShiftDate(swapWithEmployeeId, shift.getShiftDate())).thenReturn(null);

        ShiftDTO shiftDTO = new ShiftDTO();
        shiftDTO.setShiftId(shiftId);
        when(shiftMapper.toDTO(shift)).thenReturn(shiftDTO);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(null);

        ShiftStatusDTO shiftStatusDTO = new ShiftStatusDTO();
        when(shiftStatusMapper.toDTO(any(ShiftStatus.class))).thenReturn(shiftStatusDTO);

        ShiftDTO result = shiftServiceImpl.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId);

        assertEquals(shiftDTO, result);
        verify(shiftStatusRepository).save(any(ShiftStatus.class));
        verify(shiftStatusRepository).save(argThat(status ->
                status.getStatus() == ShiftStatusType.SWAP_REQUESTED &&
                        status.getRequestedSwapEmployeeId().equals(swapWithEmployeeId)
        ));
    }

    @Test
    @DisplayName("Request Shift Swap - Negative Test - Shift Not Found")
    void requestShiftSwap_Negative_ShiftNotFound() {
        Long employeeId = 1L;
        Long shiftId = 10L;
        Long swapWithEmployeeId = 2L;

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftServiceImpl.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId));
    }

    @Test
    @DisplayName("Request Shift Swap - Negative Test - Employee Not Found")
    void requestShiftSwap_Negative_EmployeeNotFound() {
        Long employeeId = 1L;
        Long shiftId = 10L;
        Long swapWithEmployeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(employeeClient.getEmployeeById(employeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

       
        assertThrows(EmployeeNotFoundException.class, () -> shiftServiceImpl.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId));
    }

    @Test
    @DisplayName("Request Shift Swap - Negative Test - Unauthorized Manager")
    void requestShiftSwap_Negative_UnauthorizedManager() {
        Long employeeId = 1L;
        Long shiftId = 10L;
        Long swapWithEmployeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(100L);

        EmployeeDTO swapEmployee = new EmployeeDTO();
        swapEmployee.setId(swapWithEmployeeId);
        swapEmployee.setManagerId(200L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);
        when(employeeClient.getEmployeeById(swapWithEmployeeId)).thenReturn(swapEmployee);

        assertThrows(UnauthorizedManagerException.class, () -> shiftServiceImpl.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId));
    }

    @Test
    @DisplayName("Request Shift Swap - Negative Test - Swap Conflict")
    void requestShiftSwap_Negative_SwapConflict() {
        Long employeeId = 1L;
        Long shiftId = 10L;
        Long swapWithEmployeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(100L);

        EmployeeDTO swapEmployee = new EmployeeDTO();
        swapEmployee.setId(swapWithEmployeeId);
        swapEmployee.setManagerId(100L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);
        when(employeeClient.getEmployeeById(swapWithEmployeeId)).thenReturn(swapEmployee);

        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(swapWithEmployeeId, shift.getShiftDate(), shift.getShiftTime())).thenReturn(true);

        assertThrows(SwapConflictException.class, () -> shiftServiceImpl.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId));
    }

    @Test
    @DisplayName("Request Shift Swap - Negative Test - Swap Employee Shift Completed")
    void requestShiftSwap_Negative_SwapEmployeeShiftCompleted() {
        Long employeeId = 1L;
        Long shiftId = 10L;
        Long swapWithEmployeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(100L);

        EmployeeDTO swapEmployee = new EmployeeDTO();
        swapEmployee.setId(swapWithEmployeeId);
        swapEmployee.setManagerId(100L);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);
        when(employeeClient.getEmployeeById(swapWithEmployeeId)).thenReturn(swapEmployee);

        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(swapWithEmployeeId, shift.getShiftDate(), shift.getShiftTime())).thenReturn(false);

        Shift swapEmployeeShift = new Shift();
        swapEmployeeShift.setShiftId(20L);
        when(shiftRepository.findByEmployeeIdAndShiftDate(swapWithEmployeeId, shift.getShiftDate())).thenReturn(swapEmployeeShift);

        ShiftStatus swapEmployeeShiftStatus = new ShiftStatus();
        swapEmployeeShiftStatus.setShiftId(20L);
        swapEmployeeShiftStatus.setStatus(ShiftStatusType.COMPLETED);
        when(shiftStatusRepository.findByShiftId(20L)).thenReturn(swapEmployeeShiftStatus);

        assertThrows(SwapConflictException.class, () -> shiftServiceImpl.requestShiftSwap(employeeId, shiftId, swapWithEmployeeId));
    }
    
    @Test
    @DisplayName("Get Manager Swap Requests - Positive Test - Requests Found")
    void getManagerSwapRequests_Positive_RequestsFound() {
        Long managerId = 1L;

        EmployeeDTO subordinate1 = new EmployeeDTO();
        subordinate1.setId(2L);
        subordinate1.setManagerId(managerId);

        EmployeeDTO subordinate2 = new EmployeeDTO();
        subordinate2.setId(3L);
        subordinate2.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate1, subordinate2);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        ShiftStatus swap1 = new ShiftStatus();
        swap1.setShiftId(10L);
        swap1.setStatus(ShiftStatusType.SWAP_REQUESTED);

        ShiftStatus swap2 = new ShiftStatus();
        swap2.setShiftId(20L);
        swap2.setStatus(ShiftStatusType.SWAP_REQUESTED);

        List<ShiftStatus> requestedSwaps = List.of(swap1, swap2);
        when(shiftStatusRepository.findByStatus(ShiftStatusType.SWAP_REQUESTED)).thenReturn(requestedSwaps);

        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(2L);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(20L);
        shift2.setEmployeeId(3L);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(14, 0));

        when(shiftRepository.findById(10L)).thenReturn(Optional.of(shift1));
        when(shiftRepository.findById(20L)).thenReturn(Optional.of(shift2));

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        dto1.setEmployeeId(2L);
        dto1.setShiftDate(LocalDate.now());
        dto1.setShiftTime(LocalTime.of(9, 0));

        ShiftDTO dto2 = new ShiftDTO();
        dto2.setShiftId(20L);
        dto2.setEmployeeId(3L);
        dto2.setShiftDate(LocalDate.now().plusDays(1));
        dto2.setShiftTime(LocalTime.of(14, 0));

        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);
        when(shiftMapper.toDTO(shift2)).thenReturn(dto2);

        when(shiftStatusMapper.toDTO(swap1)).thenReturn(mock(ShiftStatusDTO.class));
        when(shiftStatusMapper.toDTO(swap2)).thenReturn(mock(ShiftStatusDTO.class));

        List<ShiftDTO> result = shiftServiceImpl.getManagerSwapRequests(managerId);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Get Manager Swap Requests - Negative Test - Subordinates Not Found")
    void getManagerSwapRequests_Negative_SubordinatesNotFound() {
        Long managerId = 1L;
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(new ArrayList<>());

        assertThrows(ManagerSubordinatesNotFoundException.class, () -> shiftServiceImpl.getManagerSwapRequests(managerId));
    }

    @Test
    @DisplayName("Get Manager Swap Requests - Positive Test - No Swap Requests")
    void getManagerSwapRequests_Positive_NoSwapRequests() {
        Long managerId = 1L;

        EmployeeDTO subordinate1 = new EmployeeDTO();
        subordinate1.setId(2L);
        subordinate1.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate1);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        when(shiftStatusRepository.findByStatus(ShiftStatusType.SWAP_REQUESTED)).thenReturn(new ArrayList<>());

        List<ShiftDTO> result = shiftServiceImpl.getManagerSwapRequests(managerId);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Get Manager Swap Requests - Positive Test - Shift Not Found During Mapping")
    void getManagerSwapRequests_Positive_ShiftNotFoundDuringMapping() {
        Long managerId = 1L;

        EmployeeDTO subordinate1 = new EmployeeDTO();
        subordinate1.setId(2L);
        subordinate1.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate1);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        ShiftStatus swap1 = new ShiftStatus();
        swap1.setShiftId(10L);
        swap1.setStatus(ShiftStatusType.SWAP_REQUESTED);

        List<ShiftStatus> requestedSwaps = List.of(swap1);
        when(shiftStatusRepository.findByStatus(ShiftStatusType.SWAP_REQUESTED)).thenReturn(requestedSwaps);

        when(shiftRepository.findById(10L)).thenReturn(Optional.empty());

        List<ShiftDTO> result = shiftServiceImpl.getManagerSwapRequests(managerId);

        assertTrue(result.isEmpty());
    }
    @Test
    @DisplayName("Approve Shift Swap - Positive Test - Swap Approved")
    void approveShiftSwap_Positive_SwapApproved() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;
        Long originalEmployeeId = 3L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(originalEmployeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        Shift swapShift = new Shift();
        swapShift.setShiftId(20L);
        swapShift.setEmployeeId(newEmployeeId);
        swapShift.setShiftDate(LocalDate.now());
        swapShift.setShiftTime(LocalTime.of(14, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.findByEmployeeIdAndShiftDate(newEmployeeId, shift.getShiftDate())).thenReturn(swapShift);
        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(originalEmployeeId, swapShift.getShiftDate(), swapShift.getShiftTime())).thenReturn(false);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(originalEmployeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(originalEmployeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setRequestedSwapEmployeeId(newEmployeeId);
        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);

        ShiftDTO shiftDTO = new ShiftDTO();
        when(shiftMapper.toDTO(shift)).thenReturn(shiftDTO);

        ShiftStatusDTO shiftStatusDTO = new ShiftStatusDTO();
        when(shiftStatusMapper.toDTO(shiftStatus)).thenReturn(shiftStatusDTO);

        ShiftDTO result = shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId);

        assertEquals(shiftDTO, result);
        assertEquals(newEmployeeId, shift.getEmployeeId());
        assertEquals(originalEmployeeId, swapShift.getEmployeeId());
        assertEquals(ShiftStatusType.SWAP_REQUEST_APPROVED, shiftStatus.getStatus());
        assertNull(shiftStatus.getRequestedSwapEmployeeId());
        verify(shiftRepository, times(2)).save(any(Shift.class));
        verify(shiftStatusRepository).save(shiftStatus);
    }

    @Test
    @DisplayName("Approve Shift Swap - Negative Test - Shift Not Found")
    void approveShiftSwap_Negative_ShiftNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId));
    }

    @Test
    @DisplayName("Approve Shift Swap - Negative Test - Employee Not Found")
    void approveShiftSwap_Negative_EmployeeNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;
        Long originalEmployeeId = 3L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(originalEmployeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(employeeClient.getEmployeeById(originalEmployeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        assertThrows(EmployeeNotFoundException.class, () -> shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId));
    }

    @Test
    @DisplayName("Approve Shift Swap - Negative Test - Swap Request Not Found")
    void approveShiftSwap_Negative_SwapRequestNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;
        Long originalEmployeeId = 3L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(originalEmployeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(originalEmployeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(originalEmployeeId)).thenReturn(employee);
        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(null);

        assertThrows(SwapRequestNotFoundException.class, () -> shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId));
    }

    @Test
    @DisplayName("Approve Shift Swap - Negative Test - Invalid Swap Employee")
    void approveShiftSwap_Negative_InvalidSwapEmployee() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;
        Long originalEmployeeId = 3L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(originalEmployeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(originalEmployeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(originalEmployeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setRequestedSwapEmployeeId(4L);

        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);

        assertThrows(InvalidSwapEmployeeException.class, () -> shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId));
    }

    @Test
    @DisplayName("Approve Shift Swap - Negative Test - Swap Shift Not Found")
    void approveShiftSwap_Negative_SwapShiftNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;
        Long originalEmployeeId = 3L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(originalEmployeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        EmployeeDTO employee = new EmployeeDTO();
    
        employee.setId(originalEmployeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(originalEmployeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setRequestedSwapEmployeeId(newEmployeeId);

        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);
        when(shiftRepository.findByEmployeeIdAndShiftDate(newEmployeeId, shift.getShiftDate())).thenReturn(null);

        assertThrows(SwapShiftNotFoundException.class, () -> shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId));
    }

    @Test
    @DisplayName("Approve Shift Swap - Negative Test - Swap Conflict")
    void approveShiftSwap_Negative_SwapConflict() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long newEmployeeId = 2L;
        Long originalEmployeeId = 3L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(originalEmployeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        Shift swapShift = new Shift();
        swapShift.setShiftId(20L);
        swapShift.setEmployeeId(newEmployeeId);
        swapShift.setShiftDate(LocalDate.now());
        swapShift.setShiftTime(LocalTime.of(14, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.findByEmployeeIdAndShiftDate(newEmployeeId, shift.getShiftDate())).thenReturn(swapShift);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(originalEmployeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(originalEmployeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setRequestedSwapEmployeeId(newEmployeeId);
        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);

        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(originalEmployeeId, swapShift.getShiftDate(), swapShift.getShiftTime())).thenReturn(true);

        assertThrows(SwapConflictException.class, () -> shiftServiceImpl.approveShiftSwap(managerId, shiftId, newEmployeeId));
    }
    @Test
    @DisplayName("Reject Shift Swap - Positive Test - Swap Rejected")
    void rejectShiftSwap_Positive_SwapRejected() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setStatus(ShiftStatusType.SWAP_REQUESTED);

        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);

        ShiftDTO shiftDTO = new ShiftDTO();
        when(shiftMapper.toDTO(shift)).thenReturn(shiftDTO);

        ShiftStatusDTO shiftStatusDTO = new ShiftStatusDTO();
        when(shiftStatusMapper.toDTO(shiftStatus)).thenReturn(shiftStatusDTO);

        ShiftDTO result = shiftServiceImpl.rejectShiftSwap(managerId, shiftId);

        assertEquals(shiftDTO, result);
        assertEquals(ShiftStatusType.SWAP_REQUEST_REJECTED, shiftStatus.getStatus());
        verify(shiftStatusRepository).save(shiftStatus);
    }

    @Test
    @DisplayName("Reject Shift Swap - Negative Test - Shift Not Found")
    void rejectShiftSwap_Negative_ShiftNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftServiceImpl.rejectShiftSwap(managerId, shiftId));
    }

    @Test
    @DisplayName("Reject Shift Swap - Negative Test - Employee Not Found")
    void rejectShiftSwap_Negative_EmployeeNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(employeeClient.getEmployeeById(employeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        assertThrows(EmployeeNotFoundException.class, () -> shiftServiceImpl.rejectShiftSwap(managerId, shiftId));
    }

    @Test
    @DisplayName("Reject Shift Swap - Negative Test - Swap Request Not Found")
    void rejectShiftSwap_Negative_SwapRequestNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);
        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(null);

        assertThrows(SwapRequestNotFoundException.class, () -> shiftServiceImpl.rejectShiftSwap(managerId, shiftId));
    }

    @Test
    @DisplayName("Reject Shift Swap - Negative Test - Swap Request Not Requested State")
    void rejectShiftSwap_Negative_SwapRequestNotRequestedState() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setStatus(ShiftStatusType.SCHEDULED);

        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);

        assertThrows(SwapRequestNotFoundException.class, () -> shiftServiceImpl.rejectShiftSwap(managerId, shiftId));
    }
    
    @Test
    @DisplayName("Update Shift - Positive Test - Shift Updated")
    void updateShift_Positive_ShiftUpdated() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newTime = LocalTime.of(10, 0);

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, newDate, newTime)).thenReturn(false);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setId(1L);
        shiftStatus.setShiftId(shiftId);
        shiftStatus.setStatus(ShiftStatusType.OPEN);
        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(shiftStatus);

        ShiftDTO shiftDTO = new ShiftDTO();
        when(shiftMapper.toDTO(shift)).thenReturn(shiftDTO);

        ShiftStatusDTO shiftStatusDTO = new ShiftStatusDTO();
        when(shiftStatusMapper.toDTO(shiftStatus)).thenReturn(shiftStatusDTO);

        ShiftDTO result = shiftServiceImpl.updateShift(managerId, shiftId, newDate, newTime);

        assertEquals(shiftDTO, result);
        assertEquals(newDate, shift.getShiftDate());
        assertEquals(newTime, shift.getShiftTime());
        verify(shiftRepository).save(shift);
        
        
    }

    @Test
    @DisplayName("Update Shift - Negative Test - Shift Not Found")
    void updateShift_Negative_ShiftNotFound() {
        // Arrange
        Long managerId = 1L;
        Long shiftId = 10L;
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newTime = LocalTime.of(10, 0);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftServiceImpl.updateShift(managerId, shiftId, newDate, newTime));
    }

    @Test
    @DisplayName("Update Shift - Negative Test - Employee Not Found")
    void updateShift_Negative_EmployeeNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newTime = LocalTime.of(10, 0);

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(employeeClient.getEmployeeById(employeeId)).thenThrow(new EmployeeNotFoundException("Employee not found"));

        assertThrows(EmployeeNotFoundException.class, () -> shiftServiceImpl.updateShift(managerId, shiftId, newDate, newTime));
    }

    @Test
    @DisplayName("Update Shift - Negative Test - Shift Overlap")
    void updateShift_Negative_ShiftOverlap() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newTime = LocalTime.of(10, 0);

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, newDate, newTime)).thenReturn(true);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        assertThrows(ShiftOverlapException.class, () -> shiftServiceImpl.updateShift(managerId, shiftId, newDate, newTime));
    }

    @Test
    @DisplayName("Update Shift - Positive Test - Create Shift Status If Null")
    void updateShift_Positive_CreateShiftStatusIfNull() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;
        LocalDate newDate = LocalDate.now().plusDays(1);
        LocalTime newTime = LocalTime.of(10, 0);

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, newDate, newTime)).thenReturn(false);

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        when(shiftStatusRepository.findByShiftId(shiftId)).thenReturn(null);

        ShiftDTO shiftDTO = new ShiftDTO();
        when(shiftMapper.toDTO(shift)).thenReturn(shiftDTO);

        ShiftStatusDTO shiftStatusDTO = new ShiftStatusDTO();
        when(shiftStatusMapper.toDTO(any(ShiftStatus.class))).thenReturn(shiftStatusDTO);

        ShiftDTO result = shiftServiceImpl.updateShift(managerId, shiftId, newDate, newTime);

        assertEquals(shiftDTO, result);
         
    }
    @Test
    @DisplayName("Delete Shift - Positive Test - Shift Deleted")
    void deleteShift_Positive_ShiftDeleted() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);
        shift.setShiftDate(LocalDate.now());
        shift.setShiftTime(LocalTime.of(9, 0));

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(managerId);

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        shiftServiceImpl.deleteShift(managerId, shiftId);

        verify(shiftRepository).deleteById(shiftId);
        verify(shiftStatusRepository).deleteByShiftId(shiftId);
    }

    @Test
    @DisplayName("Delete Shift - Negative Test - Shift Not Found")
    void deleteShift_Negative_ShiftNotFound() {
        Long managerId = 1L;
        Long shiftId = 10L;

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.empty());

        assertThrows(ShiftNotFoundException.class, () -> shiftServiceImpl.deleteShift(managerId, shiftId));
    }

    @Test
    @DisplayName("Delete Shift - Negative Test - Unauthorized Manager")
    void deleteShift_Negative_UnauthorizedManager() {
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));

        EmployeeDTO employee = new EmployeeDTO();
        employee.setId(employeeId);
        employee.setManagerId(3L); // Different manager ID

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(employee);

        assertThrows(UnauthorizedManagerException.class, () -> shiftServiceImpl.deleteShift(managerId, shiftId));
    }

    @Test
    @DisplayName("Delete Shift - Negative Test - Employee Not Found")
    void deleteShift_Negative_EmployeeNotFound() {
        // Arrange
        Long managerId = 1L;
        Long shiftId = 10L;
        Long employeeId = 2L;

        Shift shift = new Shift();
        shift.setShiftId(shiftId);
        shift.setEmployeeId(employeeId);

        when(shiftRepository.findById(shiftId)).thenReturn(Optional.of(shift));
        when(employeeClient.getEmployeeById(employeeId)).thenThrow(new RuntimeException("Employee not found"));

        assertThrows(RuntimeException.class, () -> shiftServiceImpl.deleteShift(managerId, shiftId));
    }
    
    @Test
    @DisplayName("View Manager Employee Shifts - Positive Test - Shifts Found")
    void viewManagerEmployeeShifts_Positive_ShiftsFound() {
        Long managerId = 1L;
        Long employeeId = 2L;

        EmployeeDTO subordinate = new EmployeeDTO();
        subordinate.setId(employeeId);
        subordinate.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(employeeId);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        Shift shift2 = new Shift();
        shift2.setShiftId(20L);
        shift2.setEmployeeId(employeeId);
        shift2.setShiftDate(LocalDate.now().plusDays(1));
        shift2.setShiftTime(LocalTime.of(14, 0));

        List<Shift> shifts = List.of(shift1, shift2);
        when(shiftRepository.findByEmployeeId(employeeId)).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);

        ShiftDTO dto2 = new ShiftDTO();
        dto2.setShiftId(20L);
        when(shiftMapper.toDTO(shift2)).thenReturn(dto2);

        ShiftStatus shiftStatus1 = new ShiftStatus();
        shiftStatus1.setShiftId(10L);
        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(shiftStatus1);

        ShiftStatus shiftStatus2 = new ShiftStatus();
        shiftStatus2.setShiftId(20L);
        when(shiftStatusRepository.findByShiftId(20L)).thenReturn(shiftStatus2);

        List<ShiftDTO> result = shiftServiceImpl.viewManagerEmployeeShifts(managerId, employeeId);

        assertEquals(2, result.size());
        verify(shiftStatusRepository, times(2)).findByShiftId(anyLong());
    }

    @Test
    @DisplayName("View Manager Employee Shifts - Negative Test - Unauthorized Manager")
    void viewManagerEmployeeShifts_Negative_UnauthorizedManager() {
        Long managerId = 1L;
        Long employeeId = 2L;

        List<EmployeeDTO> subordinates = new ArrayList<>();
        EmployeeDTO otherSubordinate = new EmployeeDTO();
        otherSubordinate.setId(3L); 
        subordinates.add(otherSubordinate);

        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        assertThrows(UnauthorizedManagerException.class, () -> shiftServiceImpl.viewManagerEmployeeShifts(managerId, employeeId));
    }

    @Test
    @DisplayName("View Manager Employee Shifts - Positive Test - No Shifts Found")
    void viewManagerEmployeeShifts_Positive_NoShiftsFound() {
        Long managerId = 1L;
        Long employeeId = 2L;

        EmployeeDTO subordinate = new EmployeeDTO();
        subordinate.setId(employeeId);
        subordinate.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        when(shiftRepository.findByEmployeeId(employeeId)).thenReturn(new ArrayList<>());

       
        List<ShiftDTO> result = shiftServiceImpl.viewManagerEmployeeShifts(managerId, employeeId);

       
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("View Manager Employee Shifts - Positive Test - Shift Status OPEN")
    void viewManagerEmployeeShifts_Positive_ShiftStatusNull() {

        Long managerId = 1L;
        Long employeeId = 2L;

        EmployeeDTO subordinate = new EmployeeDTO();
        subordinate.setId(employeeId);
        subordinate.setManagerId(managerId);

        List<EmployeeDTO> subordinates = List.of(subordinate);
        when(employeeClient.getEmployeesByManager(managerId)).thenReturn(subordinates);

        Shift shift1 = new Shift();
        shift1.setShiftId(10L);
        shift1.setEmployeeId(employeeId);
        shift1.setShiftDate(LocalDate.now());
        shift1.setShiftTime(LocalTime.of(9, 0));

        List<Shift> shifts = List.of(shift1);
        when(shiftRepository.findByEmployeeId(employeeId)).thenReturn(shifts);

        ShiftDTO dto1 = new ShiftDTO();
        dto1.setShiftId(10L);
        when(shiftMapper.toDTO(shift1)).thenReturn(dto1);

        when(shiftStatusRepository.findByShiftId(10L)).thenReturn(null);

        List<ShiftDTO> result = shiftServiceImpl.viewManagerEmployeeShifts(managerId, employeeId);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getShiftStatus());
        assertEquals(ShiftStatusType.OPEN, result.get(0).getShiftStatus().getStatus());
    }
    
    
    
   
}
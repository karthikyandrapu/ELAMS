package com.elams.service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.elams.config.ShiftDurationConfig;
import com.elams.dtos.EmployeeDTO;
import com.elams.dtos.ShiftDTO;
import com.elams.dtos.ShiftStatusDTO;
import com.elams.entities.Shift;
import com.elams.entities.ShiftStatus;
import com.elams.enums.ShiftStatusType;
import com.elams.exceptions.EmployeeClientException;
import com.elams.exceptions.EmployeeNotFoundException;
import com.elams.exceptions.EmployeeShiftsNotFoundException;
import com.elams.exceptions.InvalidSwapEmployeeException;
import com.elams.exceptions.ManagerNotFoundException;
import com.elams.exceptions.ManagerOwnShiftsNotFoundException;
import com.elams.exceptions.ManagerSubordinatesNotFoundException;
import com.elams.exceptions.ShiftConflictException;
import com.elams.exceptions.ShiftNotFoundException;
import com.elams.exceptions.ShiftOverlapException;
import com.elams.exceptions.SwapConflictException;
import com.elams.exceptions.SwapRequestNotFoundException;
import com.elams.exceptions.SwapShiftNotFoundException;
import com.elams.exceptions.UnauthorizedManagerException;
import com.elams.feign.EmployeeServiceClient;
import com.elams.mapper.ShiftMapper;
import com.elams.mapper.ShiftStatusMapper;
import com.elams.repository.ShiftRepository;
import com.elams.repository.ShiftStatusRepository;
import feign.FeignException.FeignClientException;

/**
 * Service implementation for managing employee shifts.
 * This class provides methods for assigning, retrieving, updating, and deleting shifts,
 * as well as handling shift swap requests and viewing colleague shifts.
 */
@Service
public class ShiftServiceImpl implements ShiftService {
	
	private final ShiftDurationConfig shiftDurationConfig;
    private final EmployeeServiceClient employeeClient;
    private final ShiftRepository shiftRepository;
    private final ShiftStatusRepository shiftStatusRepository;
    private final ShiftMapper shiftMapper;
    private final ShiftStatusMapper shiftStatusMapper;
    private static final String SHIFT_NOT_FOUND_MESSAGE = "Shift not found.";
    private static final String EMPLOYEE_NOT_FOUND_OR_UNAUTHORIZED_MESSAGE = "Employee not found or not managed by manager.";

    /**
     * Constructs a new ShiftServiceImpl.
     *
     * @param employeeClient        The client for communicating with the employee service.
     * @param shiftRepository       The repository for accessing shift data.
     * @param shiftStatusRepository The repository for accessing shift status data.
     * @param shiftMapper           The mapper for converting between Shift and ShiftDTO.
     * @param shiftStatusMapper     The mapper for converting between ShiftStatus and ShiftStatusDTO.
     */
    public ShiftServiceImpl(EmployeeServiceClient employeeClient, ShiftRepository shiftRepository,
                              ShiftStatusRepository shiftStatusRepository, ShiftMapper shiftMapper,
                              ShiftStatusMapper shiftStatusMapper,ShiftDurationConfig shiftDurationConfig) {
        this.employeeClient = employeeClient;
        this.shiftRepository = shiftRepository;
        this.shiftStatusRepository = shiftStatusRepository;
        this.shiftMapper = shiftMapper;
        this.shiftStatusMapper = shiftStatusMapper;
        this.shiftDurationConfig = shiftDurationConfig;
    }

    /**
     * Retrieves an employee by ID or throws an exception if not found or an error occurs.
     *
     * @param employeeId The ID of the employee.
     * @return The EmployeeDTO.
     * @throws EmployeeNotFoundException If the employee is not found.
     * @throws EmployeeClientException   If an error occurs while retrieving the employee.
     */
    private EmployeeDTO getEmployeeOrThrow(Long employeeId) {
        try {
            EmployeeDTO employee = employeeClient.getEmployeeById(employeeId);
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee not found.");
            }
            return employee;
        } catch (FeignClientException e) {
            throw new EmployeeClientException("Error retrieving employee from employee client. " + e.getMessage());
        }
    }

    /**
     * Retrieves the subordinates of a manager or throws an exception if not found or an error occurs.
     *
     * @param managerId The ID of the manager.
     * @return A list of EmployeeDTO representing the manager's subordinates.
     * @throws ManagerSubordinatesNotFoundException If no subordinates are found for the manager.
     * @throws EmployeeClientException              If an error occurs while retrieving the subordinates.
     */
    private List<EmployeeDTO> getSubordinatesOrThrow(Long managerId) {
        try {
            List<EmployeeDTO> subordinates = employeeClient.getEmployeesByManager(managerId);
            if (subordinates == null || subordinates.isEmpty()) {
                throw new ManagerSubordinatesNotFoundException("No subordinates found for manager ID: " + managerId);
            }
            return subordinates;
        } catch (FeignClientException e) {
            throw new EmployeeClientException("Error retrieving subordinates from employee client. " + e.getMessage());
        }
    }

    /**
     * Assigns a shift to an employee.
     *
     * @param shiftDTO  The ShiftDTO containing the shift details.
     * @param managerId The ID of the manager assigning the shift.
     * @return The assigned ShiftDTO.
     * @throws UnauthorizedManagerException If the manager is not authorized to assign the shift.
     * @throws ShiftConflictException       If the employee already has a shift on the specified date.
     */
    @Override
    @Transactional
    public ShiftDTO assignShift(ShiftDTO shiftDTO, Long managerId) {
        EmployeeDTO employee = getEmployeeOrThrow(shiftDTO.getEmployeeId());

        if (!employee.getManagerId().equals(managerId)) {
            throw new UnauthorizedManagerException("Manager with ID " + managerId + " is not authorized to assign shift to employee with ID " + shiftDTO.getEmployeeId() + ".");
        }

        if (shiftRepository.existsByEmployeeIdAndShiftDate(employee.getId(), shiftDTO.getShiftDate())) {
            throw new ShiftConflictException("Employee with ID " + shiftDTO.getEmployeeId() + " already has a shift on " + shiftDTO.getShiftDate() + ".");
        }

        Shift shift = shiftMapper.toEntity(shiftDTO);
        shift.setEmployeeId(employee.getId());
        Shift savedShift = shiftRepository.save(shift);

        ShiftStatus shiftStatus = new ShiftStatus();
        shiftStatus.setShiftId(savedShift.getShiftId());
        shiftStatus.setStatus(ShiftStatusType.SCHEDULED);
        shiftStatusRepository.save(shiftStatus);

        ShiftDTO resultDto = shiftMapper.toDTO(savedShift);
        resultDto.setShiftStatus(shiftStatusMapper.toDTO(shiftStatus));

        return resultDto;
    }


    /**
     * Completes assigned shifts that have passed their end time.
     * This method is scheduled to run every 120 seconds.
     */
    @Scheduled(fixedRate = 120000)
    @Transactional
    public void completeAssignedShifts() {
        List<ShiftStatus> shiftsToComplete = shiftStatusRepository.findByStatusIn(
                List.of(ShiftStatusType.SCHEDULED, ShiftStatusType.SWAP_REQUEST_REJECTED, ShiftStatusType.SWAP_REQUEST_APPROVED,ShiftStatusType.SWAPPED_WITH_ANOTHER_EMPLOYEE)
        );
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        
        for (ShiftStatus shiftStatus : shiftsToComplete) {
            Shift shift = shiftRepository.findById(shiftStatus.getShiftId()).orElse(null);
            if (shift != null) {
                try {
                    LocalDate startDate = shift.getShiftDate();
                    LocalTime startTime = shift.getShiftTime();
                    int durationHours = shiftDurationConfig.getDurationHours();

                    LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
                    LocalDateTime endDateTime = startDateTime.plusHours(durationHours);
                    ZonedDateTime shiftEndTime = ZonedDateTime.of(endDateTime, ZoneId.systemDefault());

                    if (shiftEndTime.isBefore(now)) {
                        shiftStatus.setStatus(ShiftStatusType.COMPLETED);
                        shiftStatusRepository.save(shiftStatus);
                    }
                } catch (DateTimeException e) {
                                    throw new DateTimeException(".Please verify the shift date, time, and duration configurations. Error: " + e.getMessage());
                }
            } 
        }
    }
    /**
     * Retrieves the shifts of an employee's colleagues on a specific date.
     *
     * @param employeeId The ID of the employee whose colleagues' shifts are requested
     * @param shiftDate  The date of the shifts.
     * @return A list of ShiftDTO representing the colleague's shifts.
     * @throws IllegalArgumentException If the shift date is null.
     * @throws ManagerNotFoundException   If the manager ID is not found for the employee.
     */
    @Override
    public List<ShiftDTO> getColleagueShifts(Long employeeId, LocalDate shiftDate) {
        if (shiftDate == null) {
            throw new IllegalArgumentException("Shift date cannot be null.");
        }
        EmployeeDTO employee = getEmployeeOrThrow(employeeId);
        Long managerId = employee.getManagerId();
        if (managerId == null) {
            throw new ManagerNotFoundException("Manager ID not found for employee ID: " + employeeId);
        }
        List<EmployeeDTO> colleagues = getSubordinatesOrThrow(managerId);
        List<Shift> filteredShifts = filterColleagueShifts(shiftRepository.findByShiftDate(shiftDate), colleagues, employeeId, managerId);
        return filteredShifts.stream()
                .map(shiftMapper::toDTO)
                .toList();
    }

    /**
     * Filters a list of shifts to only include those of colleagues.
     *
     * @param shifts      The list of shifts to filter.
     * @param colleagues  The list of colleagues.
     * @param employeeId  The ID of the employee whose colleagues' shifts are requested.
     * @param managerId   The ID of the manager.
     * @return A list of filtered Shift entities.
     */
    private List<Shift> filterColleagueShifts(List<Shift> shifts, List<EmployeeDTO> colleagues, Long employeeId, Long managerId) {
        return shifts.stream()
                .filter(shift -> colleagues.stream()
                        .anyMatch(colleague -> {
                            EmployeeDTO shiftEmployee = employeeClient.getEmployeeById(shift.getEmployeeId());
                            return shiftEmployee != null && shiftEmployee.getManagerId() != null && shiftEmployee.getManagerId().equals(managerId);
                        })
                )
                .filter(shift -> !shift.getEmployeeId().equals(employeeId))
                .toList();
    }

    /**
     * Requests a shift swap between two employees.
     *
     * @param employeeId        The ID of the employee requesting the swap.
     * @param shiftId           The ID of the shift to be swapped.
     * @param swapWithEmployeeId The ID of the employee to swap with.
     * @return The updated ShiftDTO representing the shift swap request.
     * @throws ShiftNotFoundException       If the shift is not found or not owned by the employee.
     * @throws EmployeeNotFoundException    If the employee or swap employee is not found.
     * @throws UnauthorizedManagerException If the employees do not have the same manager.
     * @throws SwapConflictException        If the swap employee already has a shift at the requested time or if the swap employee's shift is completed.
     */
    @Override
    @Transactional
    public ShiftDTO requestShiftSwap(Long employeeId, Long shiftId, Long swapWithEmployeeId) {
        Shift shift = shiftRepository.findById(shiftId).orElse(null);
        if (shift == null || !shift.getEmployeeId().equals(employeeId)) {
            throw new ShiftNotFoundException("Shift not found or not owned by employee.");
        }
        EmployeeDTO employee = getEmployeeOrThrow(employeeId);
        EmployeeDTO swapEmployee = getEmployeeOrThrow(swapWithEmployeeId);
        if (employee == null || swapEmployee == null) {
            throw new EmployeeNotFoundException("Employee or swap employee not found.");
        }
        if (!employee.getManagerId().equals(swapEmployee.getManagerId())) {
            throw new UnauthorizedManagerException("Employees must have the same manager.");
        }
        boolean hasOverlap = shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(
                swapWithEmployeeId, shift.getShiftDate(), shift.getShiftTime());
        if (hasOverlap) {
            throw new SwapConflictException("Swap employee already has a shift at this time.");
        }
        Shift swapEmployeeShift = shiftRepository.findByEmployeeIdAndShiftDate(swapWithEmployeeId, shift.getShiftDate());
        if (swapEmployeeShift != null) {
            ShiftStatus swapEmployeeShiftStatus = shiftStatusRepository.findByShiftId(swapEmployeeShift.getShiftId());
            if (swapEmployeeShiftStatus != null && swapEmployeeShiftStatus.getStatus() == ShiftStatusType.COMPLETED) {
                throw new SwapConflictException("Swap employee's shift is already completed.");
            }
        }
        ShiftDTO shiftDTO = shiftMapper.toDTO(shift);
        ShiftStatus shiftStatus = shiftStatusRepository.findByShiftId(shiftId);
        if (shiftStatus == null) {
            shiftStatus = new ShiftStatus();
            shiftStatus.setShiftId(shiftId);
        }
        shiftStatus.setStatus(ShiftStatusType.SWAP_REQUESTED);
        shiftStatus.setRequestedSwapEmployeeId(swapWithEmployeeId);
        shiftStatusRepository.save(shiftStatus);
        shiftDTO.setShiftStatus(shiftStatusMapper.toDTO(shiftStatus));
        return shiftDTO;
    }

    /**
     * Retrieves all pending swap requests for a manager.
     *
     * @param managerId The ID of the manager.
     * @return A list of ShiftDTO representing the pending swap requests.
     * @throws ManagerSubordinatesNotFoundException If no subordinates are found for the manager.
     * @throws EmployeeClientException              If an error occurs while retrieving the subordinates.
     */
    @Override
    public List<ShiftDTO> getManagerSwapRequests(Long managerId) {
        List<EmployeeDTO> subordinates = getSubordinatesOrThrow(managerId);
        List<ShiftStatus> requestedSwaps = shiftStatusRepository.findByStatus(ShiftStatusType.SWAP_REQUESTED);
        return requestedSwaps.stream()
                .filter(swap -> subordinates.stream()
                        .anyMatch(subordinate -> {
                            Shift shift = shiftRepository.findById(swap.getShiftId()).orElse(null);
                            return shift != null && shift.getEmployeeId().equals(subordinate.getId());
                        }))
                .map(swap -> {
                    Shift shift = shiftRepository.findById(swap.getShiftId()).orElse(null);
                    if (shift == null) {
                        return null;
                    }
                    ShiftDTO dto = shiftMapper.toDTO(shift);
                    dto.setShiftStatus(shiftStatusMapper.toDTO(swap));
                    return dto;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Approves a shift swap request.
     *
     * @param managerId     The ID of the manager approving the swap.
     * @param shiftId         The ID of the shift being swapped.
     * @param newEmployeeId The ID of the employee taking the shift.
     * @return The updated ShiftDTO representing the approved shift swap.
     * @throws ShiftNotFoundException        If the shift is not found.
     * @throws EmployeeNotFoundException     If the employee is not found or not managed by the manager.
     * @throws SwapRequestNotFoundException  If the swap request is not found.
     * @throws InvalidSwapEmployeeException  If the requested swap employee ID does not match.
     * @throws SwapShiftNotFoundException    If the swap shift is not found for the requested employee, date, and time.
     * @throws SwapConflictException         If the original employee already has a shift at the swap time.
     */
    @Override
    @Transactional
    public ShiftDTO approveShiftSwap(Long managerId, Long shiftId, Long newEmployeeId) {
        Shift originalShift = shiftRepository.findById(shiftId).orElse(null);
        if (originalShift == null) throw new ShiftNotFoundException(SHIFT_NOT_FOUND_MESSAGE);
        EmployeeDTO originalEmployee = getEmployeeOrThrow(originalShift.getEmployeeId());
        if (originalEmployee == null || !originalEmployee.getManagerId().equals(managerId))
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_OR_UNAUTHORIZED_MESSAGE);
        ShiftStatus shiftStatus = shiftStatusRepository.findByShiftId(shiftId);
        if (shiftStatus == null || shiftStatus.getRequestedSwapEmployeeId() == null) {
            throw new SwapRequestNotFoundException("Swap request not found.");
        }
        Long originalEmployeeId = originalShift.getEmployeeId();
        Long requestedSwapEmployeeId = shiftStatus.getRequestedSwapEmployeeId();
        if (!requestedSwapEmployeeId.equals(newEmployeeId)) {
            throw new InvalidSwapEmployeeException("Requested swap employee id does not match");
        }
 
        LocalDate originalShiftDate = originalShift.getShiftDate();
        LocalTime originalShiftTime = originalShift.getShiftTime();
 
        Shift swapShift = shiftRepository.findByEmployeeIdAndShiftDate(newEmployeeId, originalShiftDate);
        if (swapShift == null) {
            throw new SwapShiftNotFoundException("No shift found for the requested employee on the same date.");
        }
        ShiftStatus shiftSwapStatus = shiftStatusRepository.findByShiftId(swapShift.getShiftId());

        LocalDate swapShiftDate = swapShift.getShiftDate();
        LocalTime swapShiftTime = swapShift.getShiftTime();
 
        if (shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(originalEmployeeId, swapShiftDate, swapShiftTime)) {
            throw new SwapConflictException("Original employee already has a shift at the swap time.");
        }
 
        if (shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(requestedSwapEmployeeId, originalShiftDate, originalShiftTime)) {
            throw new SwapConflictException("Swapping employee already has a shift at the original shift's time.");
        }
 
        originalShift.setShiftDate(swapShiftDate);
        originalShift.setShiftTime(swapShiftTime);
        shiftRepository.save(originalShift);
 
        swapShift.setShiftDate(originalShiftDate);
        swapShift.setShiftTime(originalShiftTime);
        shiftRepository.save(swapShift);
 
        shiftStatus.setRequestedSwapEmployeeId(null);
        shiftStatus.setStatus(ShiftStatusType.SWAP_REQUEST_APPROVED);
        shiftStatusRepository.save(shiftStatus);
        shiftSwapStatus.setStatus(ShiftStatusType.SWAPPED_WITH_ANOTHER_EMPLOYEE);
        shiftStatusRepository.save(shiftSwapStatus);

 
        ShiftDTO resultDto = shiftMapper.toDTO(originalShift);
        resultDto.setShiftStatus(shiftStatusMapper.toDTO(shiftStatus));
        return resultDto;
    }
 
 

    /**
     * Rejects a shift swap request.
     *
     * @param managerId The ID of the manager rejecting the swap.
     * @param shiftId   The ID of the shift being swapped.
     * @return The updated ShiftDTO representing the rejected shift swap.
     * @throws ShiftNotFoundException       If the shift is not found.
     * @throws EmployeeNotFoundException    If the employee is not found or not managed by the manager.
     * @throws SwapRequestNotFoundException If the swap request is not found or not in the requested state.
     */
    @Override
    @Transactional
    public ShiftDTO rejectShiftSwap(Long managerId, Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId).orElse(null);
        if (shift == null) throw new ShiftNotFoundException(SHIFT_NOT_FOUND_MESSAGE);
        EmployeeDTO employee = getEmployeeOrThrow(shift.getEmployeeId());
        if (employee == null || !employee.getManagerId().equals(managerId)) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_OR_UNAUTHORIZED_MESSAGE);
        }
        ShiftStatus shiftStatus = shiftStatusRepository.findByShiftId(shiftId);
        if (shiftStatus == null || shiftStatus.getStatus() != ShiftStatusType.SWAP_REQUESTED) {
            throw new SwapRequestNotFoundException("Swap request not found or not in requested state.");
        }
        shiftStatus.setStatus(ShiftStatusType.SWAP_REQUEST_REJECTED);
        shiftStatusRepository.save(shiftStatus);
        ShiftDTO resultDto = shiftMapper.toDTO(shift);
        resultDto.setShiftStatus(shiftStatusMapper.toDTO(shiftStatus));
        return resultDto;
    }

    /**
     * Updates an existing shift.
     *
     * @param managerId The ID of the manager updating the shift.
     * @param shiftId   The ID of the shift to update.
     * @param newDate   The new date of the shift.
     * @param newTime   The new time of the shift.
     * @return The updated ShiftDTO.
     * @throws ShiftNotFoundException     If the shift is not found.
     * @throws EmployeeNotFoundException  If the employee is not found or not managed by the manager.
     * @throws ShiftOverlapException      If a shift overlap is found for the requested employee, date, and time.
     */
    @Override
    @Transactional
    public ShiftDTO updateShift(Long managerId, Long shiftId, LocalDate newDate, LocalTime newTime) {
        Shift shift = shiftRepository.findById(shiftId).orElse(null);
        if (shift == null) throw new ShiftNotFoundException(SHIFT_NOT_FOUND_MESSAGE);
        Long employeeId = shift.getEmployeeId();
        EmployeeDTO employee = getEmployeeOrThrow(employeeId);
        if (employee == null || !employee.getManagerId().equals(managerId)) {
            throw new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_OR_UNAUTHORIZED_MESSAGE);
        }
        if (shiftRepository.existsByEmployeeIdAndShiftDateAndShiftTime(employeeId, newDate, newTime)) {
            throw new ShiftOverlapException("Shift overlap found for the requested employee, date, and time.");
        }
        LocalDate oldDate = shift.getShiftDate();
        shift.setShiftDate(newDate);
        shift.setShiftTime(newTime);
        shiftRepository.save(shift);
        ShiftStatus shiftStatus = shiftStatusRepository.findByShiftId(shiftId);
        ShiftStatusType newStatus;
        if (newDate.isBefore(oldDate)) {
            newStatus = ShiftStatusType.COMPLETED;
        } else {
            newStatus = ShiftStatusType.SCHEDULED;
        }
        if (shiftStatus == null) {
            ShiftStatusDTO shiftStatusDTO = new ShiftStatusDTO();
            shiftStatusDTO.setShiftId(shiftId);
            shiftStatusDTO.setStatus(newStatus);
            shiftStatus = shiftStatusMapper.toEntity(shiftStatusDTO);
            shiftStatusRepository.save(shiftStatus);
        } else {
            shiftStatus.setStatus(newStatus);
            shiftStatusRepository.save(shiftStatus);
        }
        ShiftDTO resultDto = shiftMapper.toDTO(shift);
        resultDto.setShiftStatus(shiftStatusMapper.toDTO(shiftStatus));
        return resultDto;
    }

    /**
     * Deletes a shift.
     *
     * @param managerId The ID of the manager deleting the shift.
     * @param shiftId   The ID of the shift to delete.
     * @throws ShiftNotFoundException       If the shift is not found.
     * @throws UnauthorizedManagerException If the manager is not authorized to delete the shift.
     */
    @Override
    @Transactional
    public void deleteShift(Long managerId, Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId).orElse(null);
        if (shift == null) throw new ShiftNotFoundException(SHIFT_NOT_FOUND_MESSAGE);
        EmployeeDTO employee = getEmployeeOrThrow(shift.getEmployeeId());
        if (employee == null || !employee.getManagerId().equals(managerId)) {
            throw new UnauthorizedManagerException("You are not authorized to delete this shift");
        }
        shiftRepository.deleteById(shiftId);
        shiftStatusRepository.deleteByShiftId(shiftId);
    }

    /**
     * Processes a list of Shift entities to create a list of ShiftDTOs with their respective ShiftStatusDTOs.
     *
     * @param shifts The list of Shift entities to process.
     * @return A list of ShiftDTOs with their ShiftStatusDTOs.
     */
    private List<ShiftDTO> processShifts(List<Shift> shifts) {
        return shifts.stream()
                .map(shiftMapper::toDTO)
                .map(dto -> {
                    ShiftStatus shiftStatus = shiftStatusRepository.findByShiftId(dto.getShiftId());
                    ShiftStatusDTO statusDTO;
                    if (shiftStatus != null) {
                        statusDTO = shiftStatusMapper.toDTO(shiftStatus);
                    } else {
                        statusDTO = new ShiftStatusDTO();
                        statusDTO.setShiftId(dto.getShiftId());
                        statusDTO.setStatus(ShiftStatusType.OPEN);
                    }
                    dto.setShiftStatus(statusDTO);
                    return dto;
                })
                .toList();
    }

    /**
     * Retrieves all shifts of a specific employee under a specific manager.
     *
     * @param managerId  The ID of the manager.
     * @param employeeId The ID of the employee.
     * @return A list of ShiftDTO representing the employee's shifts under the manager.
     * @throws UnauthorizedManagerException If the employee is not a subordinate of the manager.
     */
    @Override
    public List<ShiftDTO> viewManagerEmployeeShifts(Long managerId, Long employeeId) {
        List<EmployeeDTO> subordinates = getSubordinatesOrThrow(managerId);
        boolean isSubordinate = subordinates.stream().anyMatch(sub -> sub.getId().equals(employeeId));
        if (!isSubordinate) {
            throw new UnauthorizedManagerException("Employee is not a subordinate of the manager.");
        }
        List<Shift> shifts = shiftRepository.findByEmployeeId(employeeId);
        return processShifts(shifts);
    }

    /**
     * Retrieves all shifts assigned to a specific manager, acting as an employee.
     *
     * @param managerId The ID of the manager.
     * @return A list of ShiftDTO representing the manager's own shifts.
     * @throws ManagerOwnShiftsNotFoundException If no shifts are found for the manager.
     */
    @Override
    public List<ShiftDTO> viewManagerOwnShifts(Long managerId) {
        List<Shift> shifts = shiftRepository.findByEmployeeId(managerId);
        if (shifts == null || shifts.isEmpty()) {
            throw new ManagerOwnShiftsNotFoundException("No shifts found for manager ID: " + managerId);
        }
        return processShifts(shifts);
    }

    /**
     * Retrieves all shifts managed by a specific manager.
     *
     * @param managerId The ID of the manager.
     * @return A list of ShiftDTO representing all shifts under the manager.
     * @throws ManagerSubordinatesNotFoundException If no subordinates are found for the manager.
     * @throws EmployeeClientException              If an error occurs while retrieving the subordinates.
     */
    @Override
    public List<ShiftDTO> viewManagerShifts(Long managerId) {
        List<EmployeeDTO> subordinates = getSubordinatesOrThrow(managerId);
        List<Long> employeeIds = subordinates.stream().map(EmployeeDTO::getId).toList();
        List<Shift> shifts = shiftRepository.findByEmployeeIdIn(employeeIds);
        return processShifts(shifts);
    }

    /**
     * Retrieves all shifts assigned to a specific employee.
     *
     * @param employeeId The ID of the employee.
     * @return A list of ShiftDTO representing the employee's shifts.
     * @throws EmployeeShiftsNotFoundException If no shifts are found for the employee.
     */
    @Override
    public List<ShiftDTO> viewEmployeeShifts(Long employeeId) {
        List<Shift> shifts = shiftRepository.findByEmployeeId(employeeId);
        if (shifts == null || shifts.isEmpty()) {
            throw new EmployeeShiftsNotFoundException("No shifts found for employee ID: " + employeeId);
        }
        return processShifts(shifts);
    }
    
    /**
     * Retrieves a list of upcoming shifts for a specific employee.
     *
     * @param employeeId The ID of the employee whose upcoming shifts are being retrieved.
     * @return A list of {@link ShiftDTO} objects representing the employee's upcoming shifts.
     *         The shifts are ordered by shift date in ascending order, and within the same date, by shift time in ascending order.
     * @throws EntityNotFoundException if the employee with the given ID does not exist.
     */
    @Override
    public List<ShiftDTO> viewUpcomingEmployeeShifts(Long employeeId) {
        getEmployeeOrThrow(employeeId); // Ensure employee exists
        List<Shift> upcomingShifts = shiftRepository.findByEmployeeIdAndShiftDateGreaterThanEqualOrderByShiftDateAscShiftTimeAsc(
                employeeId, LocalDate.now(ZoneId.of("Asia/Kolkata")) // Using Mumbai timezone
        );
        return processShifts(upcomingShifts);
    }
    
    /**
     * Retrieves a list of shift DTOs for a specific employee based on a single shift status.
     *
     * @param status     The {@link ShiftStatusType} to filter shifts by.
     * @param employeeId The ID of the employee whose shifts are being retrieved.
     * @return A list of {@link ShiftDTO} objects matching the specified status and employee ID.
     */
    private List<ShiftDTO> getShiftDTOsByStatusAndEmployeeId(ShiftStatusType status, Long employeeId) {
        return shiftStatusRepository.findByStatusAndEmployeeId(status, employeeId)
                .stream()
                .map(this::convertToShiftDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Retrieves a list of shift DTOs for a specific employee based on multiple shift statuses.
     *
     * @param statuses   A list of {@link ShiftStatusType} to filter shifts by.
     * @param employeeId The ID of the employee whose shifts are being retrieved.
     * @return A list of {@link ShiftDTO} objects matching the specified statuses and employee ID.
     */
    private List<ShiftDTO> getShiftDTOsByStatusesAndEmployeeId(List<ShiftStatusType> statuses, Long employeeId) {
        return shiftStatusRepository.findByStatusInAndEmployeeId(statuses, employeeId)
                .stream()
                .map(this::convertToShiftDTO)
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Converts a {@link ShiftStatus} object to a {@link ShiftDTO}.
     *
     * @param shiftStatus The {@link ShiftStatus} object to convert.
     * @return A {@link ShiftDTO} object representing the shift, or {@code null} if the shift does not exist.
     */
    private ShiftDTO convertToShiftDTO(ShiftStatus shiftStatus) {
        Optional<Shift> shiftOptional = shiftRepository.findById(shiftStatus.getShiftId());
        if (shiftOptional.isPresent()) {
            Shift shift = shiftOptional.get();
            ShiftDTO dto = shiftMapper.toDTO(shift);
            dto.setShiftStatus(shiftStatusMapper.toDTO(shiftStatus));
            return dto;
        }
        return null;
    }

    /**
     * Retrieves a list of swap requests made by a specific employee.
     *
     * @param employeeId The ID of the employee whose swap requests are being retrieved.
     * @return A list of {@link ShiftDTO} objects representing the employee's swap requests.
     */
    @Override
    public List<ShiftDTO> viewEmployeeSwapRequests(Long employeeId) {
        return getShiftDTOsByStatusesAndEmployeeId(
                List.of(ShiftStatusType.SWAP_REQUESTED, ShiftStatusType.SWAP_REQUEST_APPROVED, ShiftStatusType.SWAP_REQUEST_REJECTED),
                employeeId
        );
    }

    /**
     * Retrieves a list of rejected swap requests for a specific employee.
     *
     * @param employeeId The ID of the employee whose rejected swap requests are being retrieved.
     * @return A list of {@link ShiftDTO} objects representing the employee's rejected swap requests.
     */
    @Override
    public List<ShiftDTO> viewEmployeeRejectedSwapRequests(Long employeeId) {
        return getShiftDTOsByStatusAndEmployeeId(ShiftStatusType.SWAP_REQUEST_REJECTED, employeeId);
    }

    /**
     * Retrieves a list of approved swap requests for a specific employee.
     *
     * @param employeeId The ID of the employee whose approved swap requests are being retrieved.
     * @return A list of {@link ShiftDTO} objects representing the employee's approved swap requests.
     */
    @Override
    public List<ShiftDTO> viewEmployeeApprovedSwapRequests(Long employeeId) {
        return getShiftDTOsByStatusAndEmployeeId(ShiftStatusType.SWAP_REQUEST_APPROVED, employeeId);
    }

    /**
     * Retrieves a list of shifts swapped with another employee for a specific employee.
     *
     * @param employeeId The ID of the employee whose swapped shifts are being retrieved.
     * @return A list of {@link ShiftDTO} objects representing the shifts swapped with another employee.
     */
    @Override
    public List<ShiftDTO> viewEmployeeSwappedWithAnotherEmployee(Long employeeId) {
        return getShiftDTOsByStatusAndEmployeeId(ShiftStatusType.SWAPPED_WITH_ANOTHER_EMPLOYEE, employeeId);
    }
}
package com.elams.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.elams.dto.AttendanceReportDTO;
import com.elams.dto.AttendanceTrendsDTO;
import com.elams.entities.AttendanceReport;
import com.elams.entities.AttendanceTrends;
import com.elams.entities.LeaveType;
import com.elams.entities.Shift;
import com.elams.enums.EmployeeRole;
import com.elams.exception.AttendanceReportValidationException;
import com.elams.exception.ResourceNotFoundException;
import com.elams.repository.AttendanceReportRepository;
import com.elams.repository.AttendanceTrendsRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

/**
 * Service implementation for managing attendance reports and trends.
 * This class provides methods for creating, retrieving, updating, and deleting attendance reports,
 * as well as calculating attendance trends.
 */
@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {

  
    private  final AttendanceReportRepository attendanceReportRepository;
      
   
    private  final AttendanceTrendsRepository attendanceTrendsRepository;
    
   
    
    private Validator validator;

    
    
    private RestTemplate restTemplate;
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceReportServiceImpl.class); 


    
    /**
     * Constructs a new AttendanceReportServiceImpl with the given repositories, RestTemplate, and validator.
     *
     * @param attendanceReportRepository The repository for accessing attendance report data.
     * @param attendanceTrendsRepository The repository for accessing attendance trends data.
     * @param restTemplate               The RestTemplate for making HTTP requests to external services.
     * @param validator                  The validator for validating DTOs.
     */
      AttendanceReportServiceImpl(AttendanceReportRepository attendanceReportRepository, AttendanceTrendsRepository attendanceTrendsRepository,RestTemplate restTemplate,Validator validator) {
        this.attendanceReportRepository = attendanceReportRepository;
        this.attendanceTrendsRepository = attendanceTrendsRepository;
        this.restTemplate = restTemplate;
        this.validator= validator;
    }
      
   
   

      private static final String ATTENDANCE_SERVICE_URL = "http://localhost:8086/api/attendance";
      private static final String SHIFT_SERVICE_URL = "http://localhost:8082/shifts";
      private static final String LEAVE_BALANCE_SERVICE_URL = "http://localhost:8084/api/leave-balance";
      private static final String ATTENDANCE_REPORT_NOT_FOUND = "AttendanceReport not found for employeeId: ";
      private static final String EMPLOYEE_SERVICE_URL = "http://localhost:8083/employees";
      private static final String GOOD_ATTENDANCE = "goodAttendance";
      private static final String AVERAGE_ATTENDANCE = "averageAttendance";
      private static final String POOR_ATTENDANCE = "poorAttendance";
      
      /**
       * Retrieves all attendance reports.
       *
       * @return A list of all AttendanceReport entities.
       * @throws ResourceNotFoundException If no attendance reports are found.
       */
    @Override
    public List<AttendanceReport> getAttendanceReports() throws ResourceNotFoundException {
        List<AttendanceReport> reports = attendanceReportRepository.findAll();
        if (reports.isEmpty()) {
            throw new ResourceNotFoundException(ATTENDANCE_REPORT_NOT_FOUND);
        }
        return reports;
    }

    /**
     * Retrieves the latest attendance report for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return The latest AttendanceReport entity for the given employee ID.
     * @throws ResourceNotFoundException If no attendance reports are found for the employee.
     */
    @Override
    public AttendanceReport getAttendanceReportById(Long employeeId) throws ResourceNotFoundException {
        List<AttendanceReport> reports = attendanceReportRepository.findByEmployeeId(employeeId);

        if (reports.isEmpty()) {
            throw new ResourceNotFoundException(ATTENDANCE_REPORT_NOT_FOUND + employeeId);
        }

        
        return reports.get(reports.size() - 1);
    }
    /**
     * Retrieves all attendance reports for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return A list of AttendanceReport entities for the given employee ID.
     * @throws ResourceNotFoundException If no attendance reports are found for the employee.
     */
    @Override
    public List<AttendanceReport> getAttendanceReportsById(Long employeeId) throws ResourceNotFoundException {
        List<AttendanceReport> reports = attendanceReportRepository.findByEmployeeId(employeeId);

        if (reports.isEmpty()) {
            throw new ResourceNotFoundException(ATTENDANCE_REPORT_NOT_FOUND + employeeId);
        }

        
        return reports;
    }
    /**
     * Creates a new attendance report.
     *
     * @param attendanceReport The AttendanceReport entity to create.
     * @return The created AttendanceReport entity.
     * @throws IllegalArgumentException If the attendance report is null.
     */
    @Override
    public AttendanceReport createAttendanceReport(AttendanceReport attendanceReport) {
        if (attendanceReport == null) {
            throw new IllegalArgumentException("AttendanceReport cannot be null");
        }
        return attendanceReportRepository.save(attendanceReport);
    }

    /**
     * Deletes the latest attendance report for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return true if the report is deleted, false otherwise.
     * @throws ResourceNotFoundException If no attendance report is found for the employee.
     */
    @Override
	public boolean deleteAttendanceReport(Long employeeId) throws ResourceNotFoundException {
		
		Optional<AttendanceReport> optionalOfEmployees = attendanceReportRepository.findLatestByEmployeeId(employeeId);
		if(optionalOfEmployees.isPresent()) {
			attendanceReportRepository.deleteById(employeeId);
			return true;
		}else {
			throw new ResourceNotFoundException("AttendanceReport not Found");
		}
		
	}
    /**
     * Deletes all attendance reports for a given employee ID.
     *
     * @param employeeId The ID of the employee.
     * @return true if all reports are deleted, false otherwise.
     * @throws ResourceNotFoundException If no attendance reports are found for the employee.
     */
    @Override
	public boolean deleteAllAttendanceReportsByEmployeeId(Long employeeId) throws ResourceNotFoundException {
        List<AttendanceReport> reports = attendanceReportRepository.findByEmployeeId(employeeId);
        if (reports.isEmpty()) {
            throw new ResourceNotFoundException("No attendance reports found for employee ID: " + employeeId);
        }
        attendanceReportRepository.deleteAll(reports);
        return true;
    }
	

    /**
     * Updates the total attendance and absenteeism for the latest attendance report of a given employee.
     *
     * @param employeeId      The ID of the employee.
     * @param totalAttendance The updated total attendance count.
     * @param absenteeism     The updated absenteeism count.
     * @return true if the report is updated, false otherwise.
     * @throws ResourceNotFoundException If no attendance report is found for the employee.
     */
    @Override
    public boolean updateAttendanceReport(Long employeeId, int totalAttendance, int absenteeism) {
        AttendanceReport report = attendanceReportRepository.findLatestByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(ATTENDANCE_REPORT_NOT_FOUND+ employeeId));
        report.setTotalAttendance(totalAttendance);
        report.setAbsenteeism(absenteeism);
        attendanceReportRepository.save(report);
        return true;
    }
    
    /**
     * Calculates and creates an attendance report for a given employee and date range.
     *
     * @param employeeId The ID of the employee.
     * @param dateRange  The date range for the report (e.g., "YYYY-MM-DD to YYYY-MM-DD").
     * @param leaveType  The leave type.
     * @param role       The employee role.
     * @return The created AttendanceReportDTO.
     * @throws AttendanceReportValidationException If the attendance report DTO is invalid.
     */
    @Override
    public AttendanceReportDTO calculateAttendanceReport(Long employeeId, String dateRange,LeaveType leaveType,EmployeeRole role) {
       
        int totalAttendance = calculateTotalAttendance(employeeId, dateRange);
       
        int absenteeism = calculateAbsenteeism(employeeId, dateRange,leaveType);
   



   AttendanceReportDTO report = new AttendanceReportDTO();
   report.setEmployeeId(employeeId);
   report.setDateRange(dateRange);
   report.setTotalAttendance(totalAttendance);
   report.setAbsenteeism(absenteeism);
   

   Set<ConstraintViolation<AttendanceReportDTO>> violations = validator.validate(report);

   if (!violations.isEmpty()) {
       String errorMessage = violations.stream()
               .map(ConstraintViolation::getMessage)
               .collect(Collectors.joining(", ")); 

       throw new AttendanceReportValidationException("Validation failed: " + errorMessage);
   }

   
   AttendanceReport report1 = new AttendanceReport();
   report1.setEmployeeId(report.getEmployeeId());
   report1.setDateRange(report.getDateRange());
   report1.setTotalAttendance(report.getTotalAttendance());
   report1.setAbsenteeism(report.getAbsenteeism());
  
   AttendanceReport savedReport = attendanceReportRepository.save(report1);

   
   List<Shift> shifts = getShiftsForEmployee(employeeId,role);
   
    
   AttendanceReportDTO dto = new AttendanceReportDTO();
   dto.setReportId(savedReport.getReportId()); 
   dto.setEmployeeId(savedReport.getEmployeeId());
   dto.setDateRange(savedReport.getDateRange());
   dto.setTotalAttendance(savedReport.getTotalAttendance());
   dto.setAbsenteeism(savedReport.getAbsenteeism());
   dto.setShifts(shifts);
   

  
   Double leaveBalance = getLeaveBalanceForEmployee(employeeId, leaveType);
   Map<LeaveType, Double> leaveBalances = new EnumMap<>(LeaveType.class);

   leaveBalances.put(leaveType, leaveBalance);
   dto.setLeaveBalances(leaveBalances);

   return dto;

    }
    /**
     * Calculates the total attendance count for a given employee and date range by calling an external attendance service.
     *
     * @param employeeId The ID of the employee.
     * @param dateRange  The date range for the calculation (e.g., "YYYY-MM-DD tootipi-MM-DD").
     * @return The total attendance count, or null if an error occurs.
     */
    private Integer calculateTotalAttendance(Long employeeId, String dateRange) {
        String[] dates = dateRange.split(" to ");
        LocalDate startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
        
        
       
        String url = ATTENDANCE_SERVICE_URL + "/count?employeeId=" + employeeId + "&startDate=" + startDateTime + "&endDate=" + endDateTime;

        Integer result = restTemplate.getForObject(url, Integer.class);

        if (result == null) {
            logger.warn("Attendance service returned null for employeeId: {}, dateRange: {}", employeeId, dateRange);
            return 0; // Or throw an exception, depending on your error handling policy.
        }

        return result;

        

    }
    /**
     * Retrieves the leave balance for a given employee and leave type by calling an external leave balance service.
     *
     * @param employeeId The ID of the employee.
     * @param leaveType  The type of leave.
     * @return The leave balance, or null if an error occurs.
     */
    private Double getLeaveBalanceForEmployee(Long employeeId, LeaveType leaveType) {
        String url = LEAVE_BALANCE_SERVICE_URL + "/getbalance?employeeId=" + employeeId + "&leaveType=" + leaveType;


        try {
            return restTemplate.getForObject(url, Double.class); 
        } catch (Exception e) {
            logger.error("Error fetching leave balance for employee {} and leave type {}: {}", employeeId, leaveType, e.getMessage());
            return null;
        }
    }
   

    /**
     * Calculates the absenteeism count for a given employee and date range, considering leave balance.
     *
     * @param employeeId The ID of the employee.
     * @param dateRange  The date range for the calculation (e.g., "YYYY-MM-DD to YYYY-MM-DD").
     * @param leaveType  The type of leave to consider.
     * @return The calculated absenteeism count.
     */
    private int calculateAbsenteeism(Long employeeId, String dateRange, LeaveType leaveType) {
        Double leaveBalance1 = getLeaveBalanceForEmployee(employeeId, leaveType);

        if (leaveBalance1 == null) {
        	logger.warn("Failed to get leave balance. Assuming no leave taken.");
            leaveBalance1 = 1.0; 
        }

        String[] dates = dateRange.split(" to ");
        LocalDate startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE);
        long totalWorkingDays = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek() != DayOfWeek.SATURDAY && currentDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
                totalWorkingDays++;
            }
            currentDate = currentDate.plusDays(1);
        }

        int totalAttendance = calculateTotalAttendance(employeeId, dateRange);
        double leavesTaken = 1.0 - leaveBalance1;

        
        if(leavesTaken < 0) leavesTaken = 0;
        if(leavesTaken > 1) leavesTaken = 1;

        return (int) (totalWorkingDays - (totalAttendance + leavesTaken));
    }

    /**
     * Retrieves shifts for a given employee and role from an external shift service.
     *
     * @param employeeId The ID of the employee.
     * @param role       The role of the employee (used as a header for authorization).
     * @return A list of Shift entities for the employee, or an empty list if an error occurs.
     */
    private List<Shift> getShiftsForEmployee(Long employeeId,EmployeeRole role) {
        
        String url = SHIFT_SERVICE_URL+ "/employee/" + employeeId;
      
        HttpHeaders headers = new HttpHeaders();
        headers.set("role", role.toString()); 

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Shift[]> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    requestEntity,
                    Shift[].class
            );

            Shift[] shiftsArray = responseEntity.getBody();

            if (shiftsArray != null) {
                return Arrays.asList(shiftsArray);
            }
        } catch (Exception e) {
            
        	 logger.error("Error fetching shifts from URL: {}", url, e); 
        }

        return new ArrayList<>();
        
    }
    
    /**
     * Calculates and creates attendance trends for a given employee and date range.
     *
     * @param employeeId The ID of the employee.
     * @param dateRange  The date range for the trends (e.g., "YYYY-MM-DD to YYYY-MM-DD").
     * @return The created AttendanceTrendsDTO.
     */
    @Override
    public AttendanceTrendsDTO calculateAttendanceTrends(Long employeeId, String dateRange) {
        String[] dates = dateRange.split(" to ");
        LocalDate startDate = LocalDate.parse(dates[0], DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(dates[1], DateTimeFormatter.ISO_LOCAL_DATE);

        List<AttendanceReport> reports = getAttendanceReportsByEmployeeAndDateRange(employeeId, startDate, endDate);

      
    if (reports.isEmpty()) {
        AttendanceTrendsDTO dto = new AttendanceTrendsDTO();
        dto.setOverallTrend("No data available");
        return dto;
    } else { // Put the main logic in the else block
        double totalAttendancePercentageSum = 0;
        double totalAbsenteeismPercentageSum = 0;

        for (AttendanceReport report : reports) {
            int total = report.getTotalAttendance() + report.getAbsenteeism();
            if (total > 0) {
                totalAttendancePercentageSum += ((double) report.getTotalAttendance() / total) * 100;
                totalAbsenteeismPercentageSum += ((double) report.getAbsenteeism() / total) * 100;
            }
        }

        double averageAttendancePercentage = totalAttendancePercentageSum / reports.size();
        double averageAbsenteeismPercentage = totalAbsenteeismPercentageSum / reports.size();

        String overallTrend = determineTrend(averageAttendancePercentage);

        AttendanceTrends trends = new AttendanceTrends();
        trends.setEmployeeId(employeeId);
        trends.setDateRange(dateRange);
        trends.setAverageAttendancePercentage(averageAttendancePercentage);
        trends.setAverageAbsenteeismPercentage(averageAbsenteeismPercentage);
        trends.setOverallTrend(overallTrend);
        AttendanceTrends savedTrends = attendanceTrendsRepository.save(trends);

        AttendanceTrendsDTO dto = new AttendanceTrendsDTO();
        dto.setTrendId(savedTrends.getTrendId());
        dto.setEmployeeId(savedTrends.getEmployeeId());
        dto.setDateRange(savedTrends.getDateRange());
        dto.setAverageAttendancePercentage(savedTrends.getAverageAttendancePercentage());
        dto.setAverageAbsenteeismPercentage(savedTrends.getAverageAbsenteeismPercentage());
        dto.setOverallTrend(savedTrends.getOverallTrend());

        return dto;
    }
  }

    /**
     * Retrieves attendance reports for a given employee and date range.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start date of the date range.
     * @param endDate    The end date of the date range.
     * @return A list of AttendanceReport entities within the specified date range.
     */
    private List<AttendanceReport> getAttendanceReportsByEmployeeAndDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        List<AttendanceReport> allReports = attendanceReportRepository.findByEmployeeId(employeeId);
        List<AttendanceReport> filteredReports = new ArrayList<>();

        for (AttendanceReport report : allReports) {
            String[] reportDates = report.getDateRange().split(" to ");
            LocalDate reportStartDate = LocalDate.parse(reportDates[0], DateTimeFormatter.ISO_LOCAL_DATE);
            LocalDate reportEndDate = LocalDate.parse(reportDates[1], DateTimeFormatter.ISO_LOCAL_DATE);

            if ((reportStartDate.isEqual(startDate) || reportStartDate.isAfter(startDate)) &&
                (reportEndDate.isEqual(endDate) || reportEndDate.isBefore(endDate))) {
                filteredReports.add(report);
            }
        }
        return filteredReports;
    }
    

    private String determineTrend(double averageAttendancePercentage) {
        if (averageAttendancePercentage > 80) {
            return "Good Attendance";
        } else if (averageAttendancePercentage >= 60) {
            return "Average Attendance";
        } else {
            return "Poor Attendance";
        }
    }
    /**
     * Deletes all attendance trends associated with a specific employee.
     *
     * @param employeeId The ID of the employee whose attendance trends should be deleted.
     */
    @Override
    public void deleteTrendsByEmployeeId(Long employeeId) {
        attendanceTrendsRepository.deleteByEmployeeId(employeeId);
    }
    
    /**
     * Retrieves a map of employees categorized by their attendance trends, optionally filtered by a manager ID.
     *
     * @param managerIdFromController The manager ID to filter employees by, or null if no filter is needed.
     * @return A map where keys are attendance trend categories ("goodAttendance", "averageAttendance", "poorAttendance")
     * and values are lists of employee IDs, or a map with an error message if the manager ID does not match.
     */
    @Override
    
    public Map<String, List<Long>> listEmployeesByAttendanceTrend(Long managerIdFromController) {
        List<AttendanceTrends> allTrends = attendanceTrendsRepository.findAll();
        Map<String, List<Long>> employeesByTrend = new HashMap<>();
        
      
        employeesByTrend.put(GOOD_ATTENDANCE, new ArrayList<>());
        employeesByTrend.put(AVERAGE_ATTENDANCE, new ArrayList<>());
        employeesByTrend.put(POOR_ATTENDANCE, new ArrayList<>());

        if (allTrends != null) {
            for (AttendanceTrends trend : allTrends) {
                switch (trend.getOverallTrend()) {
                    case "Good Attendance":
                        employeesByTrend.get(GOOD_ATTENDANCE).add(trend.getEmployeeId());
                        break;
                    case "Average Attendance":
                        employeesByTrend.get(AVERAGE_ATTENDANCE).add(trend.getEmployeeId());
                        break;
                    case "Poor Attendance":
                        employeesByTrend.get(POOR_ATTENDANCE).add(trend.getEmployeeId());
                        break;
                    default:
                        logger.warn("Encountered unexpected attendance trend: {}", trend.getOverallTrend());
                        break;
                }
            }
        }else {
        	logger.error("Attendance trends repository returned null unexpectedly.");
            return employeesByTrend; // Return an empty map
        }

        List<Long> goodAttendanceEmployeeIds = employeesByTrend.get(GOOD_ATTENDANCE);
        List<Long> averageAttendanceEmployeeIds = employeesByTrend.get(AVERAGE_ATTENDANCE);
        List<Long> poorAttendanceEmployeeIds = employeesByTrend.get(POOR_ATTENDANCE);

        // Gather all employee IDs into a single list
        List<Long> allEmployeeIds = new ArrayList<>();
        allEmployeeIds.addAll(goodAttendanceEmployeeIds);
        allEmployeeIds.addAll(averageAttendanceEmployeeIds);
        allEmployeeIds.addAll(poorAttendanceEmployeeIds);

        

        // Get manager ID for all employee IDs
        Optional<Long> managerIdOptional = getManagerId(allEmployeeIds);
       

        // Check if the manager ID from the controller matches the retrieved manager ID
        
        if (managerIdFromController != null) {
            if (managerIdOptional.isPresent() && managerIdOptional.get().equals(managerIdFromController)) { // Removed null check
                return employeesByTrend;
            }else {
                Map<String, List<Long>> errorMap = new HashMap<>();
                errorMap.put("message", List.of(-1L)); // Indicate manager ID mismatch or no manager found
                return errorMap;
            }
        }

        return employeesByTrend; // Return the map if no manager ID from controller
    }

    /**
     * Retrieves the manager ID for a list of employee IDs by making an HTTP request to the employee service.
     *
     * @param employeeIds The list of employee IDs.
     * @return An Optional containing the manager ID, or an empty Optional if no manager is found or an error occurs.
     */
    private Optional<Long> getManagerId(List<Long> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Optional.empty();
        }

        String url = EMPLOYEE_SERVICE_URL + "/managers?employeeIds=" +
                employeeIds.stream().map(Object::toString).collect(Collectors.joining("&employeeIds="));

        try {
            ResponseEntity<Optional<Long>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Optional<Long>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody() != null ? response.getBody() : Optional.empty();
            } else {
                logger.warn("Failed to retrieve manager ID from employee service. Status: {}", response.getStatusCode());
                return Optional.empty();
            }
        } catch (Exception e) {
            logger.error("Error retrieving manager ID from employee service", e);
            return Optional.empty();
        }
        
    }
    

}
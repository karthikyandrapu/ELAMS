package com.elams.controller;

import com.elams.dtos.AttendanceDTO;
import com.elams.dtos.ClockInOutDTO;
import com.elams.enums.EmployeeRole;
import com.elams.exception.AlreadyClockedInException;
import com.elams.exception.ClockInNotFoundException;
import com.elams.exception.InvalidEmployeeIdException;
import com.elams.service.AttendanceService;
import com.elams.aop.AppLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing employee attendance.
 * Provides endpoints for clocking in/out, retrieving attendance records, and counting attendance.
 */
@Tag(name = "Attendance", description = "Operations related to employee attendance")
@RestController
@CrossOrigin(origins ="http://localhost:4200")
@RequestMapping("api/attendance")
public class AttendanceController {

    private static final Logger logger = AppLogger.getLogger(AttendanceController.class);

    @Autowired
    private AttendanceService attendanceService;

    @Operation(summary = "Clock in an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Clocked In", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AttendanceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Employee Id", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "Already Clocked In Today", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping("/clockin")
    public ResponseEntity<?> clockIn(@RequestBody ClockInOutDTO request) {
        logger.info("Clock-in request received for employee ID: {}", request.getEmployeeId());
        if (request.getEmployeeId() == null || request.getEmployeeId() <= 0) {
            logger.warn("Invalid employee ID provided: {}", request.getEmployeeId());
            return ResponseEntity.badRequest().body("Employee Id is Invalid");
        }
        try {
            attendanceService.clockIn(request.getEmployeeId());
            logger.info("Employee ID: {} successfully clocked in.", request.getEmployeeId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully Clocked In");
            return ResponseEntity.ok(response);
        } catch (AlreadyClockedInException e) {
            logger.warn("Employee ID: {} already clocked in today.", request.getEmployeeId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already Clocked In Today");
        } catch (InvalidEmployeeIdException e) {
            logger.error("Invalid employee ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Clocks out an employee. Updates the clock-out time if already clocked out.
     *
     * @param request The clock-out request containing the employee ID.
     * @return A ResponseEntity with the updated AttendanceDTO and success message.
     */
    @Operation(summary = "Clock out an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Clocked Out. Latest clock-out time updated.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AttendanceDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Employee Id", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No Active Clock In Found Today", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @PostMapping("/clockout")
    public ResponseEntity<?> clockOut(@RequestBody ClockInOutDTO request) {
        logger.info("Clock-out request received for employee ID: {}", request.getEmployeeId());
        if (request.getEmployeeId() == null || request.getEmployeeId() <= 0) {
            logger.warn("Invalid employee ID provided: {}", request.getEmployeeId());
            return ResponseEntity.badRequest().body("Employee Id is Invalid");
        }
        try {
            attendanceService.clockOut(request.getEmployeeId());
            logger.info("Employee ID: {} successfully clocked out.", request.getEmployeeId());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully Clocked Out");
            return ResponseEntity.ok(response);
        } catch (ClockInNotFoundException e) {
            logger.warn("No active clock-in entry found for employee ID: {} today.", request.getEmployeeId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Active Clock In Found Today");
        } catch (InvalidEmployeeIdException e) {
            logger.error("Invalid employee ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Retrieves attendance records for a specific employee.
     *
     * @param employeeId          The ID of the employee.
     * @param role                The role of the employee making the request.
     * @param currentEmployeeId The ID of the employee making the request.
     * @return A ResponseEntity containing the attendance records or an error message.
     */
    @Operation(summary = "Get attendance for an employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance records found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AttendanceDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden Access", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No attendance records found", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getAttendanceForEmployee(
            @Parameter(description = "ID of the employee") @PathVariable Long employeeId,
            @Parameter(description = "Role of the employee making the request") @RequestHeader("X-Employee-Role") EmployeeRole role,
            @Parameter(description = "ID of the employee making the request") @RequestHeader("X-Employee-Id") Long currentEmployeeId) {

        logger.info("Get attendance request received for employee ID: {}", employeeId);
        if (employeeId == null) {
            logger.warn("Employee ID cannot be null.");
            return ResponseEntity.badRequest().body("Employee ID cannot be null.");
        }

        if (!isManager(role) && !employeeId.equals(currentEmployeeId)) {
            logger.warn("Access denied for employee ID: {}. Only Managers Can Access..!!", currentEmployeeId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Only Managers Can Access..!!");
        }

        ResponseEntity<List<AttendanceDTO>> attendanceDTOs = attendanceService.getAttendanceForEmployee(employeeId);

        if (attendanceDTOs == null || attendanceDTOs.getBody() == null || attendanceDTOs.getBody().isEmpty()) {
            logger.warn("No attendance records found for employee ID: {}", employeeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No attendance records found for employee ID: " + employeeId);
        }

        logger.info("Attendance records found for employee ID: {}", employeeId);
        return ResponseEntity.ok(attendanceDTOs);
    }

    /**
     * Retrieves attendance records for the current day.
     *
     * @param role The role of the employee making the request.
     * @return A ResponseEntity containing the attendance records or an error message.
     */
    @Operation(summary = "Get attendance for today")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance records found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AttendanceDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden Access", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No attendance records found", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/today")
    public ResponseEntity<?> getAttendanceForToday(@Parameter(description = "Role of the employee making the request") @RequestHeader("X-Employee-Role") EmployeeRole role) {
        logger.info("Get attendance for today request received.");
        if (!isManager(role)) {
            logger.warn("Access denied. Only managers can access today's attendance.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Only managers can access today's attendance.");
        }
        ResponseEntity<List<AttendanceDTO>> attendanceDTOs = attendanceService.getAttendanceForToday();

        if (attendanceDTOs == null || attendanceDTOs.getBody() == null || attendanceDTOs.getBody().isEmpty()) {
            logger.warn("No attendance records found for today.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No attendance records found for today.");
        }

        logger.info("Attendance records found for today.");
        return ResponseEntity.ok(attendanceDTOs);
    }

    /**
     * Retrieves attendance records for an employee on a specific date.
     *
     * @param employeeId The ID of the employee.
     * @param date       The date for which to retrieve attendance records.
     * @param role       The role of the employee making the request.
     * @return A ResponseEntity containing the attendance records or an error message.
     */
    @Operation(summary = "Get attendance for an employee on a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance records found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AttendanceDTO.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden Access", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No attendance records found", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/employee/{employeeId}/date/{date}")
    public ResponseEntity<?> getAttendanceForEmployeeAndDate(
            @Parameter(description = "ID of the employee") @PathVariable Long employeeId,
            @Parameter(description = "Date for attendance records (YYYY-MM-DDTHH:MM:SS)") @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date,
            @Parameter(description = "Role of the employee making the request") @RequestHeader("X-Employee-Role") EmployeeRole role) {

        logger.info("Get attendance request received for employee ID: {} on date: {}", employeeId, date);
        if (employeeId == null || date == null) {
            logger.warn("Employee ID and Date cannot be null.");
            return ResponseEntity.badRequest().body("Employee ID and Date cannot be null.");
        }

        if (!isManager(role)) {
            logger.warn("Access denied. Only managers can access attendance by date.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied. Only managers can access attendance by date.");
        }

        ResponseEntity<List<AttendanceDTO>> attendanceDTOs = attendanceService.getAttendanceForEmployeeAndDate(employeeId, date);

        if (attendanceDTOs == null || attendanceDTOs.getBody() == null || attendanceDTOs.getBody().isEmpty()) {
            logger.warn("No attendance records found for employee ID: {} on date: {}", employeeId, date);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No attendance records found for employee ID: " + employeeId + " on " + date);
        }

        logger.info("Attendance records found for employee ID: {} on date: {}", employeeId, date);
        return ResponseEntity.ok(attendanceDTOs);
    }

    /**
     * Counts attendance records for an employee within a date range.
     *
     * @param employeeId The ID of the employee.
     * @param startDate  The start date of the range (YYYY-MM-DDTHH:MM:SS).
     * @param endDate    The end date of the range (YYYY-MM-DDTHH:MM:SS).
     * @return A ResponseEntity containing the count of attendance records.
     */
    @Operation(summary = "Count attendance records for an employee within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "integer"))),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid Employee ID or Date Format", content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    })
    @GetMapping("/count")
    public ResponseEntity<Integer> countAttendance(
            @Parameter(description = "ID of the employee") @RequestParam("employeeId") Long employeeId,
            @Parameter(description = "Start date for the range (YYYY-MM-DDTHH:MM:SS)") @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date for the range (YYYY-MM-DDTHH:MM:SS)") @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        logger.info("Count attendance request received for employee ID: {}, startDate: {}, endDate: {}", employeeId, startDate, endDate);
        try {
            int count = attendanceService.countAttendance(employeeId, startDate, endDate);
            logger.info("Attendance count retrieved: {}", count);
            return new ResponseEntity<>(count, HttpStatus.OK);
        } catch (InvalidEmployeeIdException e) {
            logger.error("Invalid employee ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0); // Or handle differently
        } catch (IllegalArgumentException e) {
            logger.error("Invalid date format: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0); // Or handle differently
        }
    }

    /**
     * Handles InvalidEmployeeIdException.
     *
     * @param ex The exception to handle.
     * @return A ResponseEntity with an error message.
     */
    @ExceptionHandler(InvalidEmployeeIdException.class)
    public ResponseEntity<String> handleInvalidEmployeeIdException(InvalidEmployeeIdException ex) {
        logger.error("Invalid employee ID exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Checks if the given role is a manager.
     *
     * @param role The role to check.
     * @return true if the role is a manager, false otherwise.
     */
    private boolean isManager(EmployeeRole role) {
        return role == EmployeeRole.MANAGER;
    }
    }
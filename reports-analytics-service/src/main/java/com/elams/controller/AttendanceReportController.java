package com.elams.controller;



import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.elams.dto.AttendanceReportDTO;
import com.elams.dto.AttendanceTrendsDTO;
import com.elams.dto.CalculateClockInCountDTO;
import com.elams.dto.updateAttendanceDTO;
import com.elams.entities.AttendanceReport;
import com.elams.exception.AttendanceReportCreationException;
import com.elams.exception.AttendanceReportDeletionException;
import com.elams.exception.ResourceNotFoundException;
import com.elams.service.AttendanceReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attendance-reports")
@Tag(name = "AttendanceReportManagement", description = "Calculates TotalAttendance and Absenteesim...then calculates Trends from the reports of employee..")
public class AttendanceReportController {


    private final AttendanceReportService attendanceReportService;
    
    private static final Logger logger = LoggerFactory.getLogger(AttendanceReportController.class);
    
    AttendanceReportController(AttendanceReportService attendanceReportService){
    	this.attendanceReportService = attendanceReportService;
    }
    
    
    
    
    
    /**
     * Retrieves a list of all attendance reports.
     *<p>
     *EndPoint: {@code GET/api/attendance-reports}
     * @return ResponseEntity containing a list of AttendanceReport objects.
     * @throws ResourceNotFoundException If no attendance reports are found.
     */
    @Operation(summary = "Get All Attendance Reports", description = "Retrieves a list of all attendance reports.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of attendance reports",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AttendanceReport.class)))),
            @ApiResponse(responseCode = "404", description = "Attendance reports not found", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping
    public ResponseEntity<List<AttendanceReport>> getAllAttendanceReports() {
        List<AttendanceReport> reports = attendanceReportService.getAttendanceReports();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    /**
     * Retrieves the latest attendance report for a specific employee.
     *<p>
     *EndPoint: {@code GET/api/attendance-reports/{employeeId}}
     * @param employeeId The ID of the employee.
     * @return ResponseEntity containing the AttendanceReport object.
     * @throws ResourceNotFoundException If no attendance report is found for the given employee ID.
     */
        @Operation(summary = "Get Attendance Report by Employee ID", description = "Retrieves the latest attendance report for a specific employee.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Attendance report object",
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceReport.class))),
                @ApiResponse(responseCode = "404", description = "Attendance report not found", content = @Content(mediaType = "text/plain"))
        })
        @GetMapping("/{employeeId}")
        public ResponseEntity<AttendanceReport> getAttendanceReportById(@PathVariable Long employeeId) {
            AttendanceReport report = attendanceReportService.getAttendanceReportById(employeeId);
            return new ResponseEntity<>(report, HttpStatus.OK);
        }
        
        /**
         * Retrieves a list of all attendance reports for a specific employee.
         *<p>
         *EndPoint: {@code GET/api/attendance-reports/reports/{employeeId}}
         * @param employeeId The ID of the employee.
         * @return ResponseEntity containing a list of AttendanceReport objects.
         * @throws ResourceNotFoundException If no attendance reports are found for the given employee ID.
         *
         */
        
        @Operation(summary = "Get Attendance Reports by Employee ID", description = "Retrieves a list of all attendance reports for a specific employee.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "List of attendance reports for the employee",
                        content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AttendanceReport.class)))),
                @ApiResponse(responseCode = "404", description = "Attendance reports not found for the employee", content = @Content(mediaType = "text/plain"))
        })
        @GetMapping("/reports/{employeeId}")
        public ResponseEntity<List<AttendanceReport>> getAttendanceReportsById(@PathVariable Long employeeId) {
            List<AttendanceReport> reports = attendanceReportService.getAttendanceReportsById(employeeId);
            return new ResponseEntity<>(reports, HttpStatus.OK);
        }

    
        /**
         
 * Creates a new attendance report.
 *<p>
 *EndPoint: {@code POST/api/attendance-reports}
 * @param attendanceReportDTO The AttendanceReportDTO containing the report details.
 * @return ResponseEntity with a success message or an error message.
 * @throws AttendanceReportCreationException If the attendance report creation fails.
 * @throws InvalidInputException If the input is invalid.
 * @throws Exception If a general error occurs during the creation process.
         */

     
        @Operation(summary = "Create Attendance Report", description = "Creates a new attendance report.")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Attendance report created",
                        content = @Content(mediaType = "text/plain")),
                @ApiResponse(responseCode = "406", description = "Attendance report creation failed",
                        content = @Content(mediaType = "text/plain")),
                @ApiResponse(responseCode = "400", description = "Invalid input",
                        content = @Content(mediaType = "text/plain")),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(mediaType = "text/plain"))
        })

        @PostMapping
        public ResponseEntity<String> createAttendanceReport(@Valid @RequestBody AttendanceReportDTO attendanceReportDTO) {
            logger.info("createAttendanceReport called");
            try {
                AttendanceReport attendanceReport = new AttendanceReport();
                attendanceReport.setEmployeeId(attendanceReportDTO.getEmployeeId());
                attendanceReport.setDateRange(attendanceReportDTO.getDateRange());
                attendanceReport.setTotalAttendance(attendanceReportDTO.getTotalAttendance());
                attendanceReport.setAbsenteeism(attendanceReportDTO.getAbsenteeism());
                AttendanceReport createdReport = attendanceReportService.createAttendanceReport(attendanceReport);
                logger.info("Service return value: {}", createdReport);
                if (createdReport != null) {
                    logger.info("Attendance report created successfully");
                    return new ResponseEntity<>("Attendance Report Resource Created", HttpStatus.CREATED);
                } else {
                    logger.info("Attendance report creation failed");

                    throw new AttendanceReportCreationException("Attendance Report Resource Creation failed");
                }
            } catch (Exception e) {
                logger.error("Error creating attendance report", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } finally {
                logger.info("createAttendanceReport finished");
            }
        }
    
    
        /**Updates an existing attendance report.
 *<p>
 *EndPoint: {@code PUT/api/attendance-reports/update}
 * @param updateAttendance The updateAttendanceDTO containing the updated report details.
 * @return ResponseEntity with a boolean indicating the update status.
 * @throws ResourceNotFoundException If the attendance report is not found.
 * @throws InvalidInputException If the input is invalid.
 * @throws AttendanceReportUpdateException If the attendance report update fails.
 *
     */
   
    @Operation(summary = "Update Attendance Report", description = "Updates an existing attendance report.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance report updated successfully",
                    content = @Content(mediaType = "application/json", schema = @io.swagger.v3.oas.annotations.media.Schema(type = "boolean"))),
            @ApiResponse(responseCode = "404", description = "Attendance report not found",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "text/plain"))
    })
    @PutMapping("/update")
    public ResponseEntity<Boolean> updateAttendanceReport(@Valid @RequestBody updateAttendanceDTO updateAttendance) {

            boolean updated = attendanceReportService.updateAttendanceReport(
                    updateAttendance.getEmployeeId(),
                    updateAttendance.getTotalAttendance(),
                    updateAttendance.getAbsenteeism()
            );
            return new ResponseEntity<>(updated, HttpStatus.OK);

    }

    
    /**
    
     *  Deletes an attendance report for a specific employee.
 *<p>
 *EndPoint: {@code DELETE/api/attendance-reports/{employeeId}}
 * @param employeeId The ID of the employee whose attendance report should be deleted.
 * @return ResponseEntity with a success message or an error message.
 * @throws ResourceNotFoundException If the attendance report is not found.
 * @throws AttendanceReportDeletionException If the attendance report deletion fails.
 *
     */
   
    @Operation(summary = "Delete Attendance Report", description = "Deletes the latest attendance report for a specific employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance report deleted successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Attendance report removal failed",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "Attendance report not found",
                    content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<String> deleteAttendanceReport(@PathVariable Long employeeId) {
        boolean result = attendanceReportService.deleteAttendanceReport(employeeId);
        if (result) {
            return new ResponseEntity<>("Attendance Report Resource Removed", HttpStatus.OK);
        } else {
            throw new AttendanceReportDeletionException("Attendance Report Resource Remove failed");
        }
    }
   
    /**
     *  Deletes all attendance reports for a specific employee.
 *<p>
 *EndPoint: {@code DELETE/api/attendance-reports/all/{employeeId}}
 * @param employeeId The ID of the employee whose attendance reports should be deleted.
 * @return ResponseEntity with a success message or an error message.
 * @throws ResourceNotFoundException If no attendance reports are found for the employee.
 * @throws AttendanceReportDeletionException If the deletion of attendance reports fails.
 *
     */
   
    @Operation(summary = "Delete All Attendance Reports by Employee ID", description = "Deletes all attendance reports for a specific employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All attendance reports deleted successfully",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "400", description = "Failed to remove all attendance reports",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "404", description = "No attendance reports found for the employee",
                    content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/all/{employeeId}")
    public ResponseEntity<String> deleteAllAttendanceReportsByEmployeeId(@PathVariable Long employeeId) {
        boolean result = attendanceReportService.deleteAllAttendanceReportsByEmployeeId(employeeId);
        if (result) {
            return new ResponseEntity<>("All Attendance Reports for Employee " + employeeId + " Removed", HttpStatus.OK);
        } else {
            throw new AttendanceReportDeletionException("Failed to remove all Attendance Reports for Employee " + employeeId);
        }
    }

   
     /** Calculates and returns an attendance report based on clock-in counts and leave information.
 *<p>
 *EndPoint: {@code POST/api/attendance-reports/calculate}
 * @param calculateClockCount The CalculateClockInCountDTO containing employee ID, date range, leave type, and role.
 * @return ResponseEntity containing the calculated AttendanceReportDTO.
 * @throws ResourceNotFoundException If no relevant data is found for the given employee ID and date range.
 * @throws AttendanceCalculationException If an error occurs during the attendance calculation.
 * @throws InvalidInputException If the input is invalid.
 *
     */

    
    @Operation(summary = "Calculate Attendance Report", description = "Calculates TotalAttendance, Absenteeism, leave balances, and shifts to create a new attendance report.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calculated attendance report",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceReportDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/plain"))
    })
    @PostMapping("/calculate")
    public ResponseEntity<AttendanceReportDTO> calculateAttendanceReport(@Valid @RequestBody CalculateClockInCountDTO calculateClockCount) {
        AttendanceReportDTO report = attendanceReportService.calculateAttendanceReport(calculateClockCount.getEmployeeId(), calculateClockCount.getDateRange(),calculateClockCount.getLeaveType(),calculateClockCount.getRole());
        return new ResponseEntity<>(report, HttpStatus.OK);
    }
    
   
     /**  Retrieves attendance trends for a specific employee within a given date range.
 *<p>
 *EndPoint: {@code GET/api/attendance-reports/trends/{employeeId}/{dateRange}}
 * @param employeeId The ID of the employee.
 * @param dateRange The date range for which to calculate attendance trends (e.g., "2023-01-01 to 2023-12-31").
 * @return ResponseEntity containing the AttendanceTrendsDTO with the calculated trends.
 * @throws ResourceNotFoundException If no attendance data is found for the given employee and date range.
 * @throws AttendanceTrendCalculationException If an error occurs during trend calculation.
 * @throws InvalidInputException If the input is invalid.
 *
     */
   
    
    @Operation(summary = "Get Attendance Trends", description = "Retrieves attendance trends for a specific employee within a date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance trends",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttendanceTrendsDTO.class))),
            @ApiResponse(responseCode = "404", description = "Attendance trends not found",
                    content = @Content(mediaType = "text/plain")),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/trends/{employeeId}/{dateRange}")
    public ResponseEntity<AttendanceTrendsDTO> getAttendanceTrends(
        @PathVariable Long employeeId,
        @PathVariable String dateRange) {
        AttendanceTrendsDTO trendResult = attendanceReportService.calculateAttendanceTrends(employeeId, dateRange);
        return ResponseEntity.ok(trendResult);
    }
    
    /**
     * Deletes all attendance trends for a specific employee.
     *
     * @param employeeId The ID of the employee whose attendance trends should be deleted.
     * @return ResponseEntity with HTTP status 204 (No Content) if deletion is successful,
     * or 500 (Internal Server Error) if an error occurs.
     */
    @Operation(summary = "Delete Employee Trends", description = "Deletes all attendance trends for a specific employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Attendance trends deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/trends/{employeeId}")
    public ResponseEntity<Void> deleteEmployeeTrends(@PathVariable Long employeeId) {
        try {
            attendanceReportService.deleteTrendsByEmployeeId(employeeId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Retrieves a map of employees categorized by their attendance trends.
     *
     * @return ResponseEntity with HTTP status 200 (OK) and a map of employees categorized by attendance trends.
     */
    @Operation(summary = "Get Employees by Attendance Trend", description = "Retrieves a map of employees categorized by their attendance trends (Good, Average, Poor).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Map of employees by attendance trend",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Map.class)))
    })
    @GetMapping("/employees-by-trend")
    public ResponseEntity<Map<String, List<Long>>> getEmployeesByAttendanceTrend() {
        Map<String, List<Long>> employeesByTrend = attendanceReportService.listEmployeesByAttendanceTrend();
        return ResponseEntity.ok(employeesByTrend);
    }

}
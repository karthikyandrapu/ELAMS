package com.elams.validation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elams.dto.AttendanceReportDTO;
import com.elams.entities.AttendanceReport;
import com.elams.repository.AttendanceReportRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for ensuring that an attendance report is unique based on employee ID and date range.
 * This validator checks if an attendance report with the same employee ID and date range
 * already exists in the database.
 */
@Component
public class UniqueAttendanceReportValidator implements ConstraintValidator<UniqueAttendanceReport, AttendanceReportDTO> {

    private final AttendanceReportRepository attendanceReportRepository;
    private static final Logger logger = LoggerFactory.getLogger(UniqueAttendanceReportValidator.class);

    /**
     * Constructs a new UniqueAttendanceReportValidator with the given AttendanceReportRepository.
     *
     * @param attendanceReportRepository The repository for accessing attendance report data.
     */
    public UniqueAttendanceReportValidator(AttendanceReportRepository attendanceReportRepository) {
        this.attendanceReportRepository = attendanceReportRepository;
    }

    /**
     * Checks if the given attendance report DTO is valid (i.e., unique).
     *
     * @param dto     The AttendanceReportDTO to validate.
     * @param context The ConstraintValidatorContext.
     * @return true if the DTO is valid, false otherwise.
     */
    @Override
    public boolean isValid(AttendanceReportDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getEmployeeId() == null || dto.getDateRange() == null) {
            return true; // Handle null cases separately
        }

        try {
            List<AttendanceReport> existingReports = attendanceReportRepository.findByEmployeeId(dto.getEmployeeId());

            if (existingReports != null) {
                for (AttendanceReport report : existingReports) {
                    if (report.getDateRange().equals(dto.getDateRange())) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error validating unique attendance report", e);
            return true; 
        }
    }
}
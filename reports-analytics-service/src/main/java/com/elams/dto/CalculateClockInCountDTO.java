package com.elams.dto;

import com.elams.entities.LeaveType;
import com.elams.enums.EmployeeRole;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing the parameters for calculating clock-in counts.
 * This DTO is used to transfer data required for attendance calculations between layers.
 * It includes employee ID, date range, leave type, and employee role.
 */
@Data
@Getter
@Setter
@NoArgsConstructor
public class CalculateClockInCountDTO {

    /**
     * The identifier of the employee for whom clock-in counts are calculated.
     * Cannot be null.
     */
    @NotNull(message = "Employee ID cannot be null")
    private Long employeeId;

    /**
     * The date range for which clock-in counts are calculated.
     * Cannot be null.
     */
    @NotNull(message = "Date range cannot be null")
    private String dateRange;

    /**
     * The leave type to be considered during the calculation.
     * Can be null if not applicable.
     */
    private LeaveType leaveType;

    /**
     * The role of the employee for whom clock-in counts are calculated.
     * Can be null if not applicable.
     */
    private EmployeeRole role;
}

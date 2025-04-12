package com.elams.dtos;

import com.elams.enums.EmployeeRole;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing employee information.
 * This class encapsulates the details of an employee, including their ID, name, role, and manager ID.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    /**
     * The unique identifier of the employee.
     */
    private Long id;

    /**
     * The name of the employee.
     * This field is mandatory and cannot be null.
     * The name must be between 2 and 100 characters in length.
     */
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, max = 100, message = "Name must be between 2 to 100")
    private String name;

    /**
     * The role of the employee (e.g., MANAGER, EMPLOYEE).
     * This field is mandatory and cannot be null.
     */
    @NotNull(message = "Role cannot be null")
    private EmployeeRole role;

    /**
     * The unique identifier of the employee's manager.
     * This field is mandatory and cannot be null.
     */
    @NotNull(message = "Mananger ID cannot be null")
    private Long managerId;
}
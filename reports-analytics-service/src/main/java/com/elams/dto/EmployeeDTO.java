package com.elams.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) representing employee information.
 * This DTO is used to transfer employee data between different layers of the application.
 */
@NoArgsConstructor
@Data
@Getter
@Setter
public class EmployeeDTO {

    /**
     * The unique identifier for the employee.
     */
    private Long employeeId;

    /**
     * The name of the employee.
     */
    private String name;

    /**
     * The department the employee belongs to.
     */
    private String department;

    /**
     * The designation of the employee.
     */
    private String designation;

    /**
     * The identifier of the employee's manager.
     */
    private Long managerId;
}

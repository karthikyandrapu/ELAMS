package com.elams.dtos;

import com.elams.enums.EmployeeRole;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing an employee.
 * This class encapsulates employee information, including their ID, name, role, and manager ID.
 * It is used for transferring employee data between different layers of the application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private Long id;
    private String name;
    private EmployeeRole role;
    private Long managerId;
}
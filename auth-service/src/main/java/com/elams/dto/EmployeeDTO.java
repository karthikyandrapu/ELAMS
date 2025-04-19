package com.elams.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor

@AllArgsConstructor

public class EmployeeDTO {

    private Long id;

    private String name;

    private String role;

    private Long managerId; // Added managerId and changed to Long

}
 
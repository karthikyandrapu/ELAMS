package com.elams.entities;
 
import com.elams.enums.EmployeeRole;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;

import lombok.Data;

import lombok.NoArgsConstructor;
 
@Entity

@Data

@NoArgsConstructor

@AllArgsConstructor

public class Employee {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id; // Using auto-generated ID as employeeId

    private String name;

    @Enumerated(EnumType.STRING)

    private EmployeeRole role;

    private Long managerId; // Added managerId and changed to Long

}
 
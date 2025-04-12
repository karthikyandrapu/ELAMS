package com.elams.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity class representing a shift in the shift management system.
 * This class maps to the "shift_management_table" in the database.
 * It contains details about a shift, such as its ID, the employee assigned to it,
 * the date of the shift, and the time of the shift.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="shift_management_table")
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="shift_id", nullable=false)
    private Long shiftId;
    
    @Column(name="employee_id")
    private Long employeeId;
    
    @Column(name="shift_date")
    private LocalDate shiftDate;
    
    @Column(name="shift_time")
    private LocalTime shiftTime;
}
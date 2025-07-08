package com.elams.entities;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entity representing an employee's shift.
 * This entity stores shift information, including employee ID,
 * shift date, and shift time.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shift {

    /**
     * The unique identifier for the shift.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shiftId;

    /**
     * The identifier of the employee assigned to the shift.
     */
    private Long employeeId;

    /**
     * The date of the shift.
     */
    private LocalDate shiftDate;

    /**
     * The time of the shift.
     */
    private LocalTime shiftTime;
}
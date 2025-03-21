package com.elams.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing an employee's leave balance.
 * This entity stores leave balance information, including employee ID,
 * leave type, and the available balance.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LeaveBalance {

    /**
     * The unique identifier for the leave balance record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The identifier of the employee.
     */
    private Long employeeId;

    /**
     * The type of leave (e.g., SICK, VACATION, CASUAL).
     */
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    /**
     * The available leave balance.
     */
    private Double balance;
}

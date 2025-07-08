package com.elams.entities;

import com.elams.enums.ShiftStatusType;
import jakarta.persistence.Column;
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
 * Entity class representing the status of a shift in the shift management system.
 * This class maps to the "shift_status" table in the database.
 * It contains details about the status of a shift, such as its ID, the ID of the associated shift,
 * the current status of the shift, and the ID of the employee requested for a swap.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    
    @Column(name="shift_id",unique = true)
    private Long shiftId;
    
    @Column(name="shift_status")
    @Enumerated(EnumType.STRING)
    private ShiftStatusType status;
    
    @Column(name="requested_swap_employee_id")
    private Long requestedSwapEmployeeId;
}
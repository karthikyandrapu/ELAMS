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
 * Entity representing the status of an employee's shift.
 * This entity stores shift status information, including shift ID,
 * status type, and the ID of the employee requesting a swap (if applicable).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftStatus {

    /**
     * The unique identifier for the shift status record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The identifier of the shift associated with this status.
     * Must be unique to ensure one status per shift.
     */
    @Column(unique = true)
    private Long shiftId;

    /**
     * The current status of the shift (e.g., ASSIGNED, SWAP_REQUESTED, APPROVED).
     */
    @Enumerated(EnumType.STRING)
    private ShiftStatusType status;

    /**
     * The identifier of the employee who requested a swap for this shift.
     * Null if no swap request is pending or approved.
     */
    private Long requestedSwapEmployeeId;
}

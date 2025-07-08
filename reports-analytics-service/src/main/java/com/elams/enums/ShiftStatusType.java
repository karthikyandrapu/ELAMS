package com.elams.enums;

/**
 * Enumeration representing the status of a shift.
 * This enum defines the available shift statuses, such as UNASSIGNED, ASSIGNED,
 * COMPLETED, SWAP_REQUEST_REJECTED, SWAP_REQUESTED, and SWAP_REQUEST_APPROVED.
 */
public enum ShiftStatusType {

    /**
     * Indicates that the shift has not been assigned to any employee.
     */
    UNASSIGNED,

    /**
     * Indicates that the shift has been assigned to an employee.
     */
    ASSIGNED,

    /**
     * Indicates that the shift has been completed by the assigned employee.
     */
    COMPLETED,

    /**
     * Indicates that a swap request for the shift has been rejected.
     */
    SWAP_REQUEST_REJECTED,

    /**
     * Indicates that an employee has requested a swap for the shift.
     */
    SWAP_REQUESTED,

    /**
     * Indicates that a swap request for the shift has been approved.
     */
    SWAP_REQUEST_APPROVED
}
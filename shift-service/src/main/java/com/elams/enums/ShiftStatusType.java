package com.elams.enums;

/**
 * Enumeration representing the different statuses of a shift.
 * This enum defines various states a shift can be in throughout its lifecycle.
 */
public enum ShiftStatusType {
	OPEN, 
	SCHEDULED,
    COMPLETED,
    SWAP_REQUEST_REJECTED,
    SWAP_REQUESTED,
    SWAP_REQUEST_APPROVED,
    SWAPPED_WITH_ANOTHER_EMPLOYEE,
}
package com.elams.dtos;

/**
 * Data Transfer Object (DTO) for clock-in and clock-out requests.
 * This class encapsulates the employee ID used for clock-in and clock-out operations.
 */
public class ClockInOutDTO {

    /**
     * The unique identifier of the employee.
     */
    private Long employeeId;

    /**
     * Retrieves the employee ID.
     *
     * @return The employee ID.
     */
    public Long getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the employee ID.
     *
     * @param employeeId The employee ID to set.
     */
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
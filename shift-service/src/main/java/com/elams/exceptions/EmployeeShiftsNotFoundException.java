package com.elams.exceptions;

public class EmployeeShiftsNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	public EmployeeShiftsNotFoundException(String message) {
        super(message);
    }
}
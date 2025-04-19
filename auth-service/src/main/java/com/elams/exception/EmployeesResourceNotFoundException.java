package com.elams.exception;

public class EmployeesResourceNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	public EmployeesResourceNotFoundException(String message) {
		this.message=message;
	}

	public String getMessage() {
		return message;
	}
}

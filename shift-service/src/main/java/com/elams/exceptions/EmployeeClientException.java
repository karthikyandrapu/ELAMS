package com.elams.exceptions;

public class EmployeeClientException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public EmployeeClientException(String message) {
        super(message);
    }
}
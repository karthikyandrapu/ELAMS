package com.elams.exceptions;

public class ManagerOwnShiftsNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public ManagerOwnShiftsNotFoundException(String message) {
        super(message);
    }
}
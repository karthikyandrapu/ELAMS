package com.elams.exceptions;

public class ManagerSubordinatesNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public ManagerSubordinatesNotFoundException(String message) {
        super(message);
    }
}
package com.elams.exceptions;

public class InvalidSwapEmployeeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public InvalidSwapEmployeeException(String message) {
        super(message);
    }
}
package com.elams.exceptions;

public class ShiftNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public ShiftNotFoundException(String message) {
        super(message);
    }
}
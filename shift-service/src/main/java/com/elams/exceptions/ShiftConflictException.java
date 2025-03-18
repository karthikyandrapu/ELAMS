package com.elams.exceptions;

public class ShiftConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public ShiftConflictException(String message) {
        super(message);
    }
}
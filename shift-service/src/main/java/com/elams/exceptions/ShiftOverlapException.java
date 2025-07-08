package com.elams.exceptions;

public class ShiftOverlapException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public ShiftOverlapException(String message) {
        super(message);
    }
}
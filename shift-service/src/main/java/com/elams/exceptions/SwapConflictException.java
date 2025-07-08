package com.elams.exceptions;

public class SwapConflictException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public SwapConflictException(String message) {
        super(message);
    }
}
package com.elams.exceptions;

public class UnauthorizedSwapException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public UnauthorizedSwapException(String message) {
        super(message);
    }
}
package com.elams.exceptions;

public class SwapRequestNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public SwapRequestNotFoundException(String message) {
        super(message);
    }
}
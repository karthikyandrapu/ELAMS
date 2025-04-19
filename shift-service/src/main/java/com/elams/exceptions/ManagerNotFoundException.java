package com.elams.exceptions;

public class ManagerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public ManagerNotFoundException(String message) {
        super(message);
    }
}
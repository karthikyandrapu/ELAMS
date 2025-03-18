package com.elams.exceptions;

public class UnauthorizedManagerException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public UnauthorizedManagerException(String message) {
        super(message);
    }
}
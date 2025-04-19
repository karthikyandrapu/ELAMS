package com.elams.exceptions;

public class ColleaguesNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
    public ColleaguesNotFoundException(String message) {
        super(message);
    }
}
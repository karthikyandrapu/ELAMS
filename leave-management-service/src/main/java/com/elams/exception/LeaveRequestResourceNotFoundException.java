package com.elams.exception;

public class LeaveRequestResourceNotFoundException extends Exception {
	
	private  String  message;
	
	public LeaveRequestResourceNotFoundException(String message) {
		this.message=message;
	}

	public String getMessage() {
		return message;
	}
	
}

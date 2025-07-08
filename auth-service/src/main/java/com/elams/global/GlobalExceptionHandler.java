package com.elams.global;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	/*
	@ExceptionHandler(exception = Exception.class)
	public ResponseEntity<?> exceptionHandler(Exception ex){
		ResponseEntity<String> responseEntity=new ResponseEntity<String>(
				ex.getMessage(),
				HttpStatus.BAD_REQUEST);
		return responseEntity;
	}*/
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> exceptionHandler(MethodArgumentNotValidException ex){
		List<ObjectError> listOfErrors=ex.getBindingResult().getAllErrors();
		StringBuilder builder=new StringBuilder();
		for(ObjectError error:listOfErrors) {
			String message=error.getDefaultMessage();
			builder.append(message+",");
		}
		return new ResponseEntity<String>(builder.toString(),HttpStatus.BAD_REQUEST);
	}

}

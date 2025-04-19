package com.elams.globalexceptionhandler;

import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.elams.exceptions.ColleaguesNotFoundException;
import com.elams.exceptions.EmployeeClientException;
import com.elams.exceptions.EmployeeNotFoundException;
import com.elams.exceptions.EmployeeShiftsNotFoundException;
import com.elams.exceptions.InvalidSwapEmployeeException;
import com.elams.exceptions.ManagerNotFoundException;
import com.elams.exceptions.ManagerOwnShiftsNotFoundException;
import com.elams.exceptions.ManagerSubordinatesNotFoundException;
import com.elams.exceptions.ShiftConflictException;
import com.elams.exceptions.ShiftNotFoundException;
import com.elams.exceptions.ShiftOverlapException;
import com.elams.exceptions.SwapConflictException;
import com.elams.exceptions.SwapRequestNotFoundException;
import com.elams.exceptions.SwapShiftNotFoundException;
import com.elams.exceptions.UnauthorizedManagerException;
import com.elams.exceptions.UnauthorizedSwapException;

/**
 * Global exception handler for handling various exceptions across the application.
 * This class uses {@link RestControllerAdvice} to handle exceptions globally and return appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	
		/**
	     * Handles validation exceptions thrown by {@link MethodArgumentNotValidException}.
	     * Returns a map of field names to error messages.
	     *
	     * @param ex The {@link MethodArgumentNotValidException} thrown during validation.
	     * @return A map of field names to error messages.
	     */
		@ResponseStatus(HttpStatus.BAD_REQUEST)
	    @ExceptionHandler(MethodArgumentNotValidException.class)
	    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
	        Map<String, String> errors = new HashMap<>();
	        ex.getBindingResult().getFieldErrors().forEach(error ->
	            errors.put(error.getField(), error.getDefaultMessage())
	        );
	        return errors;
	    }

		/**
	     * Handles date/time parsing exceptions thrown by {@link DateTimeParseException}.
	     * Returns a map containing the error message associated with the "dateTime" field.
	     *
	     * @param ex The {@link DateTimeParseException} thrown during date/time parsing.
	     * @return A map containing the error message.
	     */
		 @ResponseStatus(HttpStatus.BAD_REQUEST)
		    @ExceptionHandler(DateTimeParseException.class)
		    public Map<String, String> handleDateTimeParseException(DateTimeParseException ex) {
		        Map<String, String> error = new HashMap<>();
		        error.put("dateTime", "Invalid format: " + ex.getParsedString());
		        return error;
		    }
	    
		 /**
		     * Handles exceptions related to employee client errors.
		     *
		     * @param ex The {@link EmployeeClientException} thrown.
		     * @return A {@link ResponseEntity} with an internal server error status and the exception message.
		     */ 
	    @ExceptionHandler(EmployeeClientException.class)
	    public ResponseEntity<String> handleEmployeeClientException(EmployeeClientException ex) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when an employee is not found.
	     *
	     * @param ex The {@link EmployeeNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(EmployeeNotFoundException.class)
	    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when employee shifts are not found.
	     *
	     * @param ex The {@link EmployeeShiftsNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(EmployeeShiftsNotFoundException.class)
	    public ResponseEntity<String> handleEmployeeShiftsNotFoundException(EmployeeShiftsNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a swap employee is invalid.
	     *
	     * @param ex The {@link InvalidSwapEmployeeException} thrown.
	     * @return A {@link ResponseEntity} with a bad request status and the exception message.
	     */
	    @ExceptionHandler(InvalidSwapEmployeeException.class)
	    public ResponseEntity<String> handleInvalidSwapEmployeeException(InvalidSwapEmployeeException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a manager is not found.
	     *
	     * @param ex The {@link ManagerNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(ManagerNotFoundException.class)
	    public ResponseEntity<String> handleManagerNotFoundException(ManagerNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a manager's own shifts are not found.
	     *
	     * @param ex The {@link ManagerOwnShiftsNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(ManagerOwnShiftsNotFoundException.class)
	    public ResponseEntity<String> handleManagerOwnShiftsNotFoundException(ManagerOwnShiftsNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a manager's subordinates are not found.
	     *
	     * @param ex The {@link ManagerSubordinatesNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(ManagerSubordinatesNotFoundException.class)
	    public ResponseEntity<String> handleManagerSubordinatesNotFoundException(ManagerSubordinatesNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a shift conflict occurs.
	     *
	     * @param ex The {@link ShiftConflictException} thrown.
	     * @return A {@link ResponseEntity} with a conflict status and the exception message.
	     */
	    @ExceptionHandler(ShiftConflictException.class)
	    public ResponseEntity<String> handleShiftConflictException(ShiftConflictException ex) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	    }

	    /**
	     * Handles exceptions when a shift is not found.
	     *
	     * @param ex The {@link ShiftNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(ShiftNotFoundException.class)
	    public ResponseEntity<String> handleShiftNotFoundException(ShiftNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }

	    /**
	     * Handles exceptions when a shift overlap occurs.
	     *
	     * @param ex The {@link ShiftOverlapException} thrown.
	     * @return A {@link ResponseEntity} with a conflict status and the exception message.
	     */
	    @ExceptionHandler(ShiftOverlapException.class)
	    public ResponseEntity<String> handleShiftOverlapException(ShiftOverlapException ex) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a swap conflict occurs.
	     *
	     * @param ex The {@link SwapConflictException} thrown.
	     * @return A {@link ResponseEntity} with a conflict status and the exception message.
	     */
	    @ExceptionHandler(SwapConflictException.class)
	    public ResponseEntity<String> handleSwapConflictException(SwapConflictException ex) {
	        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a swap request is not found.
	     *
	     * @param ex The {@link SwapRequestNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(SwapRequestNotFoundException.class)
	    public ResponseEntity<String> handleSwapRequestNotFoundException(SwapRequestNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a swap shift is not found.
	     *
	     * @param ex The {@link SwapShiftNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(SwapShiftNotFoundException.class)
	    public ResponseEntity<String> handleSwapShiftNotFoundException(SwapShiftNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a manager is unauthorized.
	     *
	     * @param ex The {@link UnauthorizedManagerException} thrown.
	     * @return A {@link ResponseEntity} with an unauthorized status and the exception message.
	     */
	    @ExceptionHandler(UnauthorizedManagerException.class)
	    public ResponseEntity<String> handleUnauthorizedManagerException(UnauthorizedManagerException ex) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when colleagues are not found.
	     *
	     * @param ex The {@link ColleaguesNotFoundException} thrown.
	     * @return A {@link ResponseEntity} with a not found status and the exception message.
	     */
	    @ExceptionHandler(ColleaguesNotFoundException.class)
	    public ResponseEntity<String> handleColleaguesNotFoundException(ColleaguesNotFoundException ex) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	    }
	    
	    /**
	     * Handles exceptions when a swap is unauthorized.
	     *
	     * @param ex The {@link UnauthorizedSwapException} thrown.
	     * @return A {@link ResponseEntity} with an unauthorized status and the exception message.
	     */
	    @ExceptionHandler(UnauthorizedSwapException.class)
	    public ResponseEntity<String> handleUnauthorizedSwapException(UnauthorizedSwapException ex) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
	    }

	    /**
	     * Handles exceptions when an illegal argument is provided.
	     *
	     * @param ex The {@link IllegalArgumentException} thrown.
	     * @return A {@link ResponseEntity} with a bad request status and the exception message.
	     */
	    @ExceptionHandler(IllegalArgumentException.class)
	    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex){
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	    }

}
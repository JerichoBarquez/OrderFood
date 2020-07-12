package com.project.delivery.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class DeliveryGlobalExceptionHandler extends ResponseEntityExceptionHandler{
	
	@ExceptionHandler
	public ResponseEntity<DeliveryErrorResponse> inputInvalid(DeliveryGlobalException ex){
		DeliveryErrorResponse error = new DeliveryErrorResponse();
		error.setError(ex.getMessage());
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
	
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<DeliveryErrorResponse> handleException(Exception ex){
		DeliveryErrorResponse error = new DeliveryErrorResponse();
		error.setError(ex.getMessage());
		return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
	
	}

}

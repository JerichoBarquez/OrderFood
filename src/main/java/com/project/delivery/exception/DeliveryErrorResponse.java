package com.project.delivery.exception;


import lombok.Data;

@Data
public class DeliveryErrorResponse {
	
	private String error;
	
	public DeliveryErrorResponse(){
	}
	public DeliveryErrorResponse(String error){
		this.error = error;
	}

}

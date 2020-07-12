package com.project.delivery.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "OrderRoute")
public class OrderDetailDto {
	
	@Id @GeneratedValue
	private int id;
	private Long distance;
	private String destination;
	private String origin;
	private String status;
	

}

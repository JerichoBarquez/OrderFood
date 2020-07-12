package com.project.delivery.dto;

import com.project.delivery.entity.OrderDetail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderOutDto {
	
	private int id;
	private float distance;
	private String status;
	
	public static OrderOutDto convert(OrderDetail orderDetailDto) {
		return OrderOutDto.builder()
				.id(orderDetailDto.getId())
				.distance(orderDetailDto.getDistance())
				.status(orderDetailDto.getStatus())
				.build();
	}
	
}

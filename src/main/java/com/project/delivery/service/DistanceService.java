package com.project.delivery.service;

import java.util.List;

import com.project.delivery.entity.OrderDetailDto;
import com.project.delivery.entity.OrderInDto;

public interface DistanceService {
	
	public OrderDetailDto calculateDistance(OrderInDto orderInDto);
	public String takeOrder(int id, String status);
	public List<OrderDetailDto> findAll(int page, int limit);

}

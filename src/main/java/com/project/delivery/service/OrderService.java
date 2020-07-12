package com.project.delivery.service;

import org.springframework.data.domain.Page;

import com.project.delivery.dto.OrderInDto;
import com.project.delivery.dto.OrderOutDto;
import com.project.delivery.entity.OrderDetail;
import com.project.delivery.specification.OrderDetailSearchCriteria;

public interface OrderService {
	
	 OrderDetail calculateDistance(OrderInDto orderInDto);
	 String takeOrder(int id, String status);
	 Page<OrderOutDto> findAll(OrderDetailSearchCriteria criteria, int page, int size);

}

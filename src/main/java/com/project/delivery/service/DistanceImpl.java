package com.project.delivery.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.project.delivery.entity.OrderDetailDto;
import com.project.delivery.entity.OrderInDto;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.repository.OrderDao;

@Service
public class DistanceImpl implements DistanceService {

	private OrderDao orderDao;

	@Autowired
	DistanceImpl(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	@Override
	public OrderDetailDto calculateDistance(OrderInDto orderInDto) {

		checkLatLang(orderInDto.getOrigin());
		checkLatLang(orderInDto.getDestination());

		OrderDetailDto orderDetailDto = new OrderDetailDto();
		try {
			DistanceMatrix distanceMatrix = DistanceMatrixImpl.getDistanceMatrix(orderInDto.getOrigin(),
					orderInDto.getDestination());
			DistanceMatrixElement[] dist = distanceMatrix.rows[0].elements;
			if (dist[0].distance == null) {
				throw new DeliveryGlobalException("No location found. Zero Result");
			}
			orderDetailDto.setDestination(distanceMatrix.destinationAddresses[0]);
			orderDetailDto.setOrigin(distanceMatrix.originAddresses[0]);
			orderDetailDto.setDistance(dist[0].distance.inMeters);
			orderDetailDto.setStatus("UNASSIGNED");
			orderDetailDto = orderDao.save(orderDetailDto);
		} catch (ApiException | InterruptedException | IOException e) {
			throw new DeliveryGlobalException(e.getMessage());
		}

		return orderDetailDto;
	}

	public static boolean checkLatLang(String[] coordinates) {
		if (Double.valueOf(coordinates[0]) < -90 || Double.valueOf(coordinates[0]) > 90) {
			throw new DeliveryGlobalException("Invalid Latitude " + coordinates[0]);
		}
		if (Double.valueOf(coordinates[1]) < -180 || Double.valueOf(coordinates[1]) > 180) {
			throw new DeliveryGlobalException("Invalid Longtitude " + coordinates[1]);
		}
		return true;
	}

	@Override
	public String takeOrder(int id, String status) {
		String stat = null;
		Optional<OrderDetailDto> optDto = orderDao.findById(id);
		if (optDto.isPresent() && optDto.get().getStatus().equalsIgnoreCase("TAKEN")) {
			throw new DeliveryGlobalException("The item was sold.");
		}
		int num = orderDao.saveByOrderId(id, status);
		if (num == 1) {
			stat = "SUCCESSFUL";
		}
		return stat;
	}

	@Override
	public List<OrderDetailDto> findAll(int page, int limit) {
		List<OrderDetailDto> list = new ArrayList();
		Pageable pageable = PageRequest.of(page, limit);
		Page<OrderDetailDto> pageList = orderDao.findAll(pageable);
		list = pageList.getContent();
		return list;
	}

}

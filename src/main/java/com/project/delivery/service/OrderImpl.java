package com.project.delivery.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.LatLng;
import com.project.delivery.dto.OrderInDto;
import com.project.delivery.dto.OrderOutDto;
import com.project.delivery.entity.OrderDetail;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.repository.OrderDao;
import com.project.delivery.specification.OrderDetailSearchCriteria;
import com.project.delivery.specification.OrderDetailSearchCriteriaSpecBuilder;
import com.project.delivery.dto.OrderStatus.Status;

@Service
public class OrderImpl implements OrderService {
	
	@Value("${api.key}")
	private String apiKey;

	private OrderDao orderDao;
	
	@Autowired
	public OrderImpl(OrderDao orderDao) {
		this.orderDao = orderDao;
	}
	
	@Override
	public OrderDetail calculateDistance(OrderInDto orderInDto) {
		OrderDetail orderDetailDto = new OrderDetail();
		try {
			LatLng latOrigin = checkLatlng(orderInDto.getOrigin());
			LatLng latDestination = checkLatlng(orderInDto.getDestination());
			DistanceMatrixImpl distImpl = new DistanceMatrixImpl();
			DistanceMatrix distanceMatrix = distImpl.getDistanceMatrix(latOrigin, latDestination, apiKey);
			DistanceMatrixElement[] dist = distanceMatrix.rows[0].elements;
			if (dist[0].distance == null) {
				throw new DeliveryGlobalException("No location found. 0 Result");
			}
			orderDetailDto.setDestination(distanceMatrix.destinationAddresses[0]);
			orderDetailDto.setOrigin(distanceMatrix.originAddresses[0]);
			orderDetailDto.setDistance(dist[0].distance.inMeters);
			orderDetailDto.setStatus(Status.UNASSIGNED.name());
			orderDetailDto = orderDao.save(orderDetailDto);
		} catch (ApiException | InterruptedException | IOException e) {
			throw new DeliveryGlobalException(e.getMessage());
		}
		return orderDetailDto;
	}
	
	public static LatLng checkLatlng(String[] coordinates) {
		try {
			if (coordinates.length == 2) {
				if (Double.valueOf(coordinates[0]) < -90 || Double.valueOf(coordinates[0]) > 90)
				    throw new DeliveryGlobalException("Invalid Latitude " + coordinates[0]);
				if (Double.valueOf(coordinates[1]) < -180 || Double.valueOf(coordinates[1]) > 180)
					throw new DeliveryGlobalException("Invalid Longitude " + coordinates[1]);
			} else 
				throw new DeliveryGlobalException("Longitude or Latitude is missing.");
		} catch (NumberFormatException e) {
			throw new DeliveryGlobalException("Invalid input");
		}
		return new LatLng(new Double(coordinates[0]), new Double(coordinates[1]));
	}


	@Override
	public String takeOrder(int id, String status) {
		String stat = "";
		Optional<OrderDetail> optDto = orderDao.findById(id);
		if (!optDto.isPresent())
			throw new DeliveryGlobalException("Item not available");
		else if (optDto.get().getStatus().equalsIgnoreCase(Status.TAKEN.name()))
			throw new DeliveryGlobalException("The item was taken/sold.");
		if (orderDao.saveByOrderId(id, status)  == 1)
			stat = Status.SUCCESSFUL.name();
		return stat;
	}

	@Override
	public Page<OrderOutDto> findAll(OrderDetailSearchCriteria criteria, int page, int limit) {
		Specification<OrderDetail> spec = OrderDetailSearchCriteriaSpecBuilder.from(criteria);
		Page<OrderDetail> results = orderDao.findAll(spec, PageRequest.of(page, limit));
		return results.map(result -> OrderOutDto.convert(result));
	}

	
}

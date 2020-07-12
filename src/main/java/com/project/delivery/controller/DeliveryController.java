package com.project.delivery.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.delivery.entity.OrderDetailDto;
import com.project.delivery.entity.OrderInDto;
import com.project.delivery.entity.OrderOutDto;
import com.project.delivery.entity.TakeOrderDto;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.service.DistanceService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

	private DistanceService distance;

	@Autowired
	DeliveryController(DistanceService distance) {
		this.distance = distance;
	}

	@PostMapping(path = "orders", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ApiOperation(value = "Creates an order", response = OrderOutDto.class)
	public ResponseEntity<OrderOutDto> order(@RequestBody OrderInDto orderInDto) {
		OrderOutDto orderOutDto = new OrderOutDto();
		OrderDetailDto orderDetailDto = null;
		orderDetailDto = distance.calculateDistance(orderInDto);
		orderOutDto.setDistance(orderDetailDto.getDistance());
		orderOutDto.setId(orderDetailDto.getId());
		orderOutDto.setStatus(orderDetailDto.getStatus());
		return new ResponseEntity<>(orderOutDto, HttpStatus.OK);
	}

	@PatchMapping(path = "orders/{id}", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<TakeOrderDto> order(@PathVariable("id") int id, @RequestBody TakeOrderDto takeOrderDto) {
		String status = distance.takeOrder(id, takeOrderDto.getStatus());
		TakeOrderDto stats = new TakeOrderDto();
		stats.setStatus(status);
		return new ResponseEntity<>(stats, HttpStatus.OK);

	}

	@GetMapping(path = "orders")
	public ResponseEntity<List<OrderOutDto>> pages(@RequestParam("page") int page, 
			@RequestParam("limit") int limit)  {
		if (page < 0) {
			throw new  DeliveryGlobalException("Invalid input. Should not be less than or equal to 0.");
		}
		List<OrderOutDto> listaPage = new ArrayList<>();
		for (OrderDetailDto orderDetailDto : distance.findAll(page, limit)) {
			OrderOutDto det = new OrderOutDto();
			det.setDistance(orderDetailDto.getDistance());
			det.setId(orderDetailDto.getId());
			det.setStatus(orderDetailDto.getStatus());
			listaPage.add(det);
		}
		return new ResponseEntity<>(listaPage, HttpStatus.OK);
	}
	
	

}

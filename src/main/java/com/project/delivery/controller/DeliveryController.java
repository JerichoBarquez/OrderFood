package com.project.delivery.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.project.delivery.dto.OrderInDto;
import com.project.delivery.dto.OrderOutDto;
import com.project.delivery.dto.TakeOrderDto;
import com.project.delivery.entity.OrderDetail;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.service.OrderService;
import com.project.delivery.specification.OrderDetailSearchCriteria;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/delivery")
@RequiredArgsConstructor
public class DeliveryController {

	private OrderService orderService;

	@Autowired
	public DeliveryController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping(path = "orders")
	@ApiOperation(value = "Creates an order", response = OrderOutDto.class)
	public ResponseEntity<OrderOutDto> order(@RequestBody OrderInDto orderInDto) {
		OrderDetail orderDetail = orderService.calculateDistance(orderInDto);
		return new ResponseEntity<>(OrderOutDto.convert(orderDetail), HttpStatus.OK);
	}

	@PatchMapping(path = "orders/{id}")
	@ApiOperation(value = "Takes an order", response = TakeOrderDto.class)
	public ResponseEntity<TakeOrderDto> order(@PathVariable("id") int id, @RequestBody TakeOrderDto takeOrderDto) {
		String status = orderService.takeOrder(id, takeOrderDto.getStatus());
		TakeOrderDto stats = new TakeOrderDto();
		stats.setStatus(status);
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}

	@GetMapping(path = "orders")
	@ApiOperation(value = "List of orders", response = OrderOutDto.class)
	public ResponseEntity<List<OrderOutDto>> pages(@RequestParam("page") int page, @RequestParam("limit") int limit) {
		if (page < 1) {
			throw new DeliveryGlobalException("Invalid input. Should not be less than 1.");
		}
		final OrderDetailSearchCriteria criteria = OrderDetailSearchCriteria.builder().build();
		List<OrderOutDto> listaPage = orderService.findAll(criteria, page - 1, limit).getContent();
		return new ResponseEntity<>(listaPage, HttpStatus.OK);
	}
}

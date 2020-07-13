package com.project.delivery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.project.delivery.controller.DeliveryController;
import com.project.delivery.dto.OrderInDto;
import com.project.delivery.dto.OrderOutDto;
import com.project.delivery.dto.TakeOrderDto;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.service.OrderService;

@SpringBootTest
class DeliveryApplicationTests {
	
	@Autowired
	DeliveryController controller;
	
	@Autowired
	OrderService orderService;
	
	/**
	 * Creates an order with blank input
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return 
	 *     Exception expected with "Invalid input" error message
	 */
	@Test
	public void whenNoInputCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"",""};
			order.setDestination(destination);
			String origin [] = {"",""};
			order.setOrigin(origin);
			controller.order(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input", e.getMessage());
		}
	}
	
	/**
	 * Creates an order with invalid location
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return 
	 *     Exception expected with "No location found. 0 Result" error message
	 */
	@Test
	public void whenNoLocationFoundCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"14.576713","121.046201"};
			order.setDestination(destination);
			String origin [] = {"14.577107","-121.04751"};
			order.setOrigin(origin);
			controller.order(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("No location found. 0 Result", e.getMessage());
		}
	}
	
	/**
	 * Creates an order with invalid input (not a number)
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return 
	 *     Exception expected with "Invalid input" error message
	 */
	@Test
	public void whenNotNumberCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"AAA","BBB"};
			order.setDestination(destination);
			String origin [] = {"14.5AA07","-121.04751"};
			order.setOrigin(origin);
			controller.order(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input", e.getMessage());
		}
	}
	
	/**
	 * Creates an order with incomplete input.
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return 
	 *     Exception expected with "Longitude or Latitude is missing" error message
	 */
	@Test
	public void whenLackOfInputCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"14.576713","121.04751"};
			order.setDestination(destination);
			String origin [] = {"14.577107"};
			order.setOrigin(origin);
			controller.order(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Longitude or Latitude is missing.", e.getMessage());
		}
	}
	
	/**
	 * Creates an order with not in range Longitude.
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return 
	 *     Exception expected with "Invalid Longitude -200.04751" error message
	 */
	@Test
	public void whenInvalidLongitudeCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"14.576713","121.04751"};
			order.setDestination(destination);
			String origin [] = {"14.577107","-200.04751"};
			order.setOrigin(origin);
			controller.order(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid Longitude -200.04751", e.getMessage());
		}
	}
	
	/**
	 * Creates an order with not in range Latitude.
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return 
	 *     Exception expected with "Invalid Latitude 100.576713" error message
	 */
	@Test
	public void whenInvalidLatitudeCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"100.576713","121.04751"};
			order.setDestination(destination);
			String origin [] = {"14.577107","121.04751"};
			order.setOrigin(origin);
			controller.order(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid Latitude 100.576713", e.getMessage());
		}
	}
	
	/**
	 * Creates an order successfully.
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return ResponseEntity<OrderOutDto>
	 *     
	 */
	@Test
	public void whenSuccessfulCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination [] = {"14.576713","121.04751"};
			order.setDestination(destination);
			String origin [] = {"14.577107","121.04751"};
			order.setOrigin(origin);
			ResponseEntity<OrderOutDto> res = controller.order(order);
			assertEquals(HttpStatus.OK, res.getStatusCode());
			assertEquals(157f, res.getBody().getDistance(),157f);
			assertEquals("UNASSIGNED", res.getBody().getStatus());	
		} catch (DeliveryGlobalException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Taken an order that is not existing.
	 * This may work in the future when the Item/Id is already existing
	 * 
	 * @param id - Item/Order
	 * @param TakeOrderDto - status
	 * @return 
	 *     Exception expected with "Item not available" error message
	 */
	@Test
	public void whenTakesAnOrderNotExisting() {
		try {
			int id = 100;
			TakeOrderDto takeOrderDto = new TakeOrderDto();
			takeOrderDto.setStatus("TAKEN");
			controller.order(id, takeOrderDto);
		} catch (DeliveryGlobalException e) {
			assertEquals("Item not available", e.getMessage());
			assertTrue(true);
		}		
	}
	
	/**
	 * Taken an order with status UNASSIGNED.
	 * This may not work in the future when the Item/Id is already TAKEN
	 * 
	 * @param id - Item/Order
	 * @param TakeOrderDto - status
	 * @return ResponseEntity<TakeOrderDto> -status
	 *     
	 */
	@Test
	public void whenTakesAnOrderUnassigned() {
		try {
			int id = 30;
			TakeOrderDto takeOrderDto = new TakeOrderDto();
			takeOrderDto.setStatus("TAKEN");
			ResponseEntity<TakeOrderDto> takeOrder = controller.order(id, takeOrderDto);
			assertEquals(HttpStatus.OK, takeOrder.getStatusCode());
			assertEquals("SUCCESSFUL", takeOrder.getBody().getStatus());
		} catch (DeliveryGlobalException e) {
			assertTrue(false);
		}		
	}
	
	/**
	 * Taken an order with status TAKEN.
	 * 
	 * @param id - Item/Order
	 * @param TakeOrderDto - status
	 * @return 
	 *     Exception expected with "The item was taken/sold." error message
	 */
	@Test
	public void whenTakesAnOrderTaken() {
		try {
			int id = 30;
			TakeOrderDto takeOrderDto = new TakeOrderDto();
			takeOrderDto.setStatus("TAKEN");
			controller.order(id, takeOrderDto);
		} catch (DeliveryGlobalException e) {
			assertEquals("The item was taken/sold.", e.getMessage());
			assertTrue(true);
		}		
	}
	
	/**
	 * Taken an order with page 0.
	 * 
	 * @param id - Item/Order
	 * @param TakeOrderDto - status
	 * @return 
	 *     Exception expected with "Invalid input. Should not be less than 1." error message
	 */
	@Test
	public void whenListOrderPageZero() {
		try {
			int page = 0;
			int limit = 2;
			controller.pages(page, limit);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input. Should not be less than 1.", e.getMessage());
		}
	}
	/**
	 * Taken an order with limit 0.
	 * 
	 * @param id - Item/Order
	 * @param TakeOrderDto - status
	 * @return 
	 *     Exception expected with "Invalid input. Should not be less than 1." error message
	 */
	@Test
	public void whenListOrderLimitZero() {
		try {
			int page = 1;
			int limit = 0;
			controller.pages(page, limit);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input. Should not be less than 1.", e.getMessage());
		}
	}
	
	/**
	 * List of orders.
	 * 
	 * @param int page 
	 * @param int limit
	 * @return ResponseEntity<List<OrderOutDto>> 
	 *     
	 */
	@Test
	public void whenListOrderSucessful() {
		try {
			int page = 1;
			int limit = 3;
			ResponseEntity<List<OrderOutDto>> res = controller.pages(page, limit);
			assertEquals(3, res.getBody().size());
			assertEquals(HttpStatus.OK, res.getStatusCode());
		} catch (DeliveryGlobalException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * List of orders (not existing page/limit).
	 * 
	 * @param int page 
	 * @param int limit
	 * @return ResponseEntity<List<OrderOutDto>> EMPTY
	 *     
	 */
	@Test
	public void whenListOrderNotExisting() {
		try {
			int page = 9;
			int limit = 10;
			ResponseEntity<List<OrderOutDto>> res = controller.pages(page, limit);
			assertEquals(0, res.getBody().size());
			assertEquals(HttpStatus.OK, res.getStatusCode());
		} catch (DeliveryGlobalException e) {
			assertTrue(false);
		}
	}

}

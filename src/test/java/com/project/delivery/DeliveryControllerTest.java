package com.project.delivery;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.project.delivery.controller.DeliveryController;
import com.project.delivery.dto.OrderInDto;
import com.project.delivery.dto.OrderOutDto;
import com.project.delivery.dto.TakeOrderDto;
import com.project.delivery.entity.OrderDetail;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.service.OrderImpl;
import com.project.delivery.service.OrderService;
import com.project.delivery.specification.OrderDetailSearchCriteria;

import junit.framework.TestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ OrderImpl.class})
public class DeliveryControllerTest extends TestCase{
	
	@InjectMocks
	DeliveryController test = new DeliveryController();
	
	@Mock
	private OrderService service;
	
	@Mock
	Page pages;
	
	/**
	 * Creates an order successfully
	 * 
	 * @param destination - latitude longitude
	 * @param origin - latitude longitude
	 * @return ResponseEntity<OrderOutDto>
	 *     
	 */
	@Test
	public void test_createorder() {
		try {
			OrderDetail detail = new OrderDetail();
			detail.setDistance(157L);
			detail.setId(1);
			detail.setStatus("UNASSIGNED");
			Mockito.when(service.calculateDistance(Mockito.any(OrderInDto.class))).thenReturn(detail);
			OrderInDto order = new OrderInDto();
			String destination [] = {"100.576713","121.04751"};
			order.setDestination(destination);
			String origin [] = {"14.577107","121.04751"};
			order.setOrigin(origin);
			ResponseEntity<OrderOutDto> res = test.order(order);
			assertEquals(157, res.getBody().getDistance(), 157);
			assertEquals("UNASSIGNED", res.getBody().getStatus());
			assertEquals(1, res.getBody().getId());
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
		}
	}
	
	/**
	 * Takes an order
	 * 
	 * @param id - latitude longitude
	 * @param TakeOrderDto.status - TAKEN
	 * @return String - SUCESSFUL
	 * 
	 */
	@Test
	public void test_takeOrder() {
		Mockito.when(service.takeOrder(Mockito.anyInt(), Mockito.anyString())).thenReturn("SUCCESSFUL");
		TakeOrderDto takeOrderDto = new TakeOrderDto();
		takeOrderDto.setStatus("TAKEN");
		int id = 1;
		ResponseEntity<TakeOrderDto> res = test.order(id, takeOrderDto);
		assertEquals("SUCCESSFUL", res.getBody().getStatus());
	}
	
	@Test
	/**
	 * List items per page and limit
	 * 
	 * @param int page
	 * @param int limit
	 * @return ResponseEntity<List<OrderOutDto>> 
	 * 
	 */
	public void test_page() {
		Mockito.when(service.findAll(Mockito.any(OrderDetailSearchCriteria.class), Mockito.anyInt(), Mockito.anyInt())).thenReturn(pages);
		List<OrderOutDto> list = new ArrayList();
		OrderOutDto order = new OrderOutDto();
		order.setDistance(123);
		order.setId(2);
		order.setStatus("UNASSIGNED");
		list.add(order);
		list.add(order);
		Mockito.when(pages.getContent()).thenReturn(list);
		int page = 1;
		int limit = 2;
		
		ResponseEntity<List<OrderOutDto>> res = test.pages(page, limit);
		assertEquals(2, res.getBody().size());
		assertEquals(123, res.getBody().get(0).getDistance(),123);
		assertEquals("UNASSIGNED", res.getBody().get(0).getStatus());
		assertEquals(2, res.getBody().get(0).getId());
	}
	
	/**
	 * List items per page and limit
	 * 
	 * @param int page
	 * @param int limit
	 * @return Excpetion with "Invalid input. Should not be less than 1." message
	 * 
	 */
	@Test
	public void test_pageException() {
		try {
			int page = 0;
			int limit = 2;
			test.pages(page, limit);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input. Should not be less than 1.", e.getMessage());
		}
		
	}
	/**
	 * List items per page and limit
	 * 
	 * @param int page
	 * @param int limit
	 * @return Exception with "Invalid input. Should not be less than 1." message
	 * 
	 */
	@Test
	public void test_page2() {
		try {
			int page = 1;
			int limit = 0;
			test.pages(page, limit);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input. Should not be less than 1.", e.getMessage());
		}
		
	}
}

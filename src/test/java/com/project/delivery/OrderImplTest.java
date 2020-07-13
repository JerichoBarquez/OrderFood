package com.project.delivery;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import com.google.maps.model.Distance;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.LatLng;
import com.project.delivery.dto.OrderInDto;
import com.project.delivery.dto.OrderOutDto;
import com.project.delivery.entity.OrderDetail;
import com.project.delivery.exception.DeliveryGlobalException;
import com.project.delivery.repository.OrderDao;
import com.project.delivery.service.DistanceMatrixImpl;
import com.project.delivery.service.OrderImpl;
import com.project.delivery.specification.OrderDetailSearchCriteriaSpecBuilder;
import junit.framework.TestCase;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ OrderImpl.class,OrderDetailSearchCriteriaSpecBuilder.class, OrderOutDto.class })
public class OrderImplTest extends TestCase {

	@InjectMocks
	OrderImpl test;

	@Mock
	OrderDao dao;
	
	@Mock
	Page page;
	
	@Mock
	Specification specs;

	@Mock
	DistanceMatrixImpl mockDistanceMatrixImpl;

	/**
	 * Creates an order with invalid location
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return Exception expected with "Invalid input" error message
	 * @throws Exception
	 */
	public void test_whenNoInputCreateOrder() {
		try {
			test = new OrderImpl(dao);
			OrderInDto order = new OrderInDto();
			String destination[] = { "", "" };
			order.setDestination(destination);
			String origin[] = { "", "" };
			order.setOrigin(origin);
			test.calculateDistance(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input", e.getMessage());
		}
	}

	/**
	 * Creates an order with not existing location
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return Exception expected with "No location found. 0 Result" error message
	 * @throws Exception
	 */
	public void test_whenNoLocationFoundCreateOrder() throws Exception {
		String[] originAddresses = { "14.576713", "121.046201" };
		String[] destinationAddresses = { "14.577107", "-121.04751" };
		DistanceMatrixRow row = new DistanceMatrixRow();
		DistanceMatrixElement elem = new DistanceMatrixElement();
		Distance dist = new Distance();
		dist.inMeters = 123;
		elem.distance = null;
		DistanceMatrixElement[] elemAr = { elem };
		row.elements = elemAr;
		DistanceMatrixRow[] rows = { row };
		DistanceMatrix mockDistanceMatrix = new DistanceMatrix(originAddresses, destinationAddresses, rows);
		PowerMockito.whenNew(DistanceMatrixImpl.class).withNoArguments().thenReturn(mockDistanceMatrixImpl);
		Mockito.when(mockDistanceMatrixImpl.getDistanceMatrix(Mockito.any(LatLng.class), Mockito.any(LatLng.class),
				Mockito.any())).thenReturn(mockDistanceMatrix);
		try {
			OrderInDto order = new OrderInDto();
			String destination[] = { "14.576713", "121.046201" };
			order.setDestination(destination);
			String origin[] = { "14.577107", "-121.04751" };
			order.setOrigin(origin);
			test.calculateDistance(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("No location found. 0 Result", e.getMessage());
		}
	}

	/**
	 * Creates an order successfully
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return OrderDetail
	 * @throws Exception
	 */
	public void test_whenCreateOrderSuccess() throws Exception {
		String[] originAddresses = { "14.576713", "121.046201" };
		String[] destinationAddresses = { "14.577107", "-121.04751" };
		DistanceMatrixRow row = new DistanceMatrixRow();
		DistanceMatrixElement elem = new DistanceMatrixElement();
		Distance dist = new Distance();
		dist.inMeters = 123;
		elem.distance = dist;
		DistanceMatrixElement[] elemAr = { elem };
		row.elements = elemAr;
		DistanceMatrixRow[] rows = { row };
		DistanceMatrix mockDistanceMatrix = new DistanceMatrix(originAddresses, destinationAddresses, rows);
		mockDistanceMatrix.destinationAddresses[0] = "Calbayog";
		mockDistanceMatrix.originAddresses[0] = "Mandaluyong";
		PowerMockito.whenNew(DistanceMatrixImpl.class).withNoArguments().thenReturn(mockDistanceMatrixImpl);
		Mockito.when(mockDistanceMatrixImpl.getDistanceMatrix(Mockito.any(LatLng.class), Mockito.any(LatLng.class),
				Mockito.any())).thenReturn(mockDistanceMatrix);
		OrderDetail detail = new OrderDetail();
		detail.setDestination("Calbayog");
		detail.setOrigin("Mandaluyong");
		detail.setStatus("UNASSIGNED");
		detail.setDistance(123L);

		Mockito.when(dao.save(Mockito.any(OrderDetail.class))).thenReturn(detail);
		try {
			OrderInDto order = new OrderInDto();
			String destination[] = { "14.576713", "121.046201" };
			order.setDestination(destination);
			String origin[] = { "14.577107", "-121.04751" };
			order.setOrigin(origin);
			OrderDetail det = test.calculateDistance(order);
			assertEquals("Calbayog", det.getDestination());
			assertEquals("Mandaluyong", det.getOrigin());
			assertEquals(123L, det.getDistance(), 123L);
		} catch (DeliveryGlobalException e) {
			assertTrue(false);

		}
	}

	/**
	 * Creates an order with invalid input (not a number)
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return Exception expected with "Invalid input" error message
	 */
	public void test_whenNotNumberCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination[] = { "AAA", "BBB" };
			order.setDestination(destination);
			String origin[] = { "14.5AA07", "-121.04751" };
			order.setOrigin(origin);
			test.calculateDistance(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid input", e.getMessage());
		}
	}

	/**
	 * Creates an order with incomplete input.
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return Exception expected with "Longitude or Latitude is missing" error
	 *         message
	 */
	public void test_whenLackOfInputCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination[] = { "14.576713", "121.04751" };
			order.setDestination(destination);
			String origin[] = { "14.577107" };
			order.setOrigin(origin);
			test.calculateDistance(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Longitude or Latitude is missing.", e.getMessage());
		}
	}

	/**
	 * Creates an order with not in range Longitude.
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return Exception expected with "Invalid Longitude -200.04751" error message
	 */
	@Test
	public void test_whenInvalidLongitudeCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination[] = { "14.576713", "121.04751" };
			order.setDestination(destination);
			String origin[] = { "14.577107", "-200.04751" };
			order.setOrigin(origin);
			test.calculateDistance(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid Longitude -200.04751", e.getMessage());
		}
	}

	/**
	 * Creates an order with not in range Latitude.
	 * 
	 * @param destination - latitude longitude
	 * @param origin      - latitude longitude
	 * @return Exception expected with "Invalid Latitude 100.576713" error message
	 */
	public void test_whenInvalidLatitudeCreateOrder() {
		try {
			OrderInDto order = new OrderInDto();
			String destination[] = { "100.576713", "121.04751" };
			order.setDestination(destination);
			String origin[] = { "14.577107", "121.04751" };
			order.setOrigin(origin);
			test.calculateDistance(order);
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Invalid Latitude 100.576713", e.getMessage());
		}
	}

	/**
	 * Takes an order that is not existing.
	 * 
	 * @param ID - item/order
	 * @param STATUS
	 * @return Exception expected with "Item not available" error message
	 */
	public void test_takeOrderNotPresent() {
		try {
			Optional<OrderDetail> opt = Optional.ofNullable(null);
			Mockito.when(dao.findById(Mockito.anyInt())).thenReturn(opt);
			test.takeOrder(1, "TAKEN");
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("Item not available", e.getMessage());
		}
	}
	/**
	 * Takes an order that is Taken/Not available.
	 * 
	 * @param ID - item/order
	 * @param STATUS
	 * @return Exception expected with "The item was taken/sold." error message
	 */
	public void test_takeOrderTaken() {
		try {
			OrderDetail detail = new OrderDetail();
			detail.setStatus("TAKEN");
			Optional<OrderDetail> opt = Optional.of(detail);
			Mockito.when(dao.findById(Mockito.anyInt())).thenReturn(opt);
			test.takeOrder(1, "TAKEN");
		} catch (DeliveryGlobalException e) {
			assertTrue(true);
			assertEquals("The item was taken/sold.", e.getMessage());
		}
	}
	/**
	 * Takes an order that is "UNASSIGNED".
	 * 
	 * @param ID - item/order
	 * @param STATUS
	 * @return String - Successful
	 */
	public void test_takeOrderUnaasigned() {
		try {
			OrderDetail detail = new OrderDetail();
			detail.setStatus("UNASSIGNED");
			Optional<OrderDetail> opt = Optional.of(detail);
			Mockito.when(dao.findById(Mockito.anyInt())).thenReturn(opt);
			Mockito.when(dao.saveByOrderId(Mockito.anyInt(), Mockito.anyString())).thenReturn(1);
			String status = test.takeOrder(1, "TAKEN");
			assertEquals("SUCCESSFUL", status);
		} catch (DeliveryGlobalException e) {
			assertTrue(false);
		}
	}
	
		
}

package com.project.delivery.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.project.delivery.entity.OrderDetailDto;

public interface OrderDao extends PagingAndSortingRepository<OrderDetailDto, Integer>{
	
	@Transactional
	@Modifying
	@Query("update OrderRoute p set p.status =:status WHERE p.id = :id")
	int saveByOrderId(@Param("id") int id, @Param("status") String status);
	
	
}

package com.project.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.delivery.entity.OrderDetail;

@Repository
public interface OrderDao extends JpaRepository<OrderDetail, Integer>, JpaSpecificationExecutor<OrderDetail>{
	
	@Transactional
	@Modifying
	@Query("update OrderDetail p set p.status =:status WHERE p.id = :id")
	int saveByOrderId(@Param("id") int id, @Param("status") String status);
	
}

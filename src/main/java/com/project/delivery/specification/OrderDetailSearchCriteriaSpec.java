package com.project.delivery.specification;

import org.springframework.data.jpa.domain.Specification;

import com.project.delivery.entity.OrderDetail;

public class OrderDetailSearchCriteriaSpec {
	
	public static Specification<OrderDetail> hasStatus(String status) {
        return (order, cq, cb) -> cb.equal(order.get("status"), status);
    }	

}

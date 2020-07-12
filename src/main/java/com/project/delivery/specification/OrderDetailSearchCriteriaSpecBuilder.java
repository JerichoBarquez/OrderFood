package com.project.delivery.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import com.project.delivery.entity.OrderDetail;

public class OrderDetailSearchCriteriaSpecBuilder {

	public static Specification<OrderDetail> from(final OrderDetailSearchCriteria filter) {

		Specification<OrderDetail> spec = Specification.where(null);
		if (!StringUtils.isEmpty(filter.getStatus())) {
			spec = spec.and(OrderDetailSearchCriteriaSpec.hasStatus(filter.getStatus()));
		}

		return spec;

	}

}

package com.ecommerce.order_service.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutbox;

public interface JpaOrderAdapter extends JpaRepository<OrderOutbox, Integer> {

}

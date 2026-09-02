package com.ecommerce.order_service.infrastructure.persistence.adapter;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxDto;

public interface JpaOrderOutboxTableAdapter extends JpaRepository<OrderOutboxDto, Integer> {

}

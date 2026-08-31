package com.ecommerce.order_service.infrastructure.persistence.adapter;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.order_service.infrastructure.persistence.dto.OrderTable;

public interface JpaOrderTableAdapter extends JpaRepository<OrderTable, Integer>{

}

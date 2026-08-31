package com.ecommerce.order_service.infrastructure.persistence.service;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.infrastructure.persistence.adapter.JpaOrderTableAdapter;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderTable;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    
    private final JpaOrderTableAdapter jpaAdapter;

    @Transactional
    public void persist(Order order) {
        OrderTable orderEntity = OrderTable.fromDomain(order);

        jpaAdapter.save(orderEntity);
    }
}

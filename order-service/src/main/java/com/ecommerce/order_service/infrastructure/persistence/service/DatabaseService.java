package com.ecommerce.order_service.infrastructure.persistence.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.infrastructure.persistence.adapter.JpaOrderTableAdapter;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    
    private final JpaOrderTableAdapter jpaAdapter;

    @Transactional
    public void persist(Order order) {
        OrderDto orderEntity = OrderDto.fromDomain(order);

        jpaAdapter.save(orderEntity);
    }

    @Transactional
    public void persist(OrderDto dto) {
        jpaAdapter.save(dto);
    }

    public Optional<OrderDto> findById(int orderId) {
        return jpaAdapter.findById(orderId);
    } 
}

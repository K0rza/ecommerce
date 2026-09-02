package com.ecommerce.order_service.infrastructure.persistence.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.infrastructure.persistence.adapter.OrderJpaRepository;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderPersistenceService {

    private final OrderJpaRepository orderRepository;

    @Transactional
    public void persist(Order order) {
        OrderDto orderEntity = OrderDto.fromDomain(order);

        orderRepository.save(orderEntity);
    }

    @Transactional
    public void persist(OrderDto dto) {
        orderRepository.save(dto);
    }

    public Optional<OrderDto> findById(int orderId) {
        return orderRepository.findById(orderId);
    }
}

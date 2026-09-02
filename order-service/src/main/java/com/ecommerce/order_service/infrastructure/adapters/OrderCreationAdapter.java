package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.application.port.OrderCreationPort;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.service.OrderPersistenceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OrderCreationAdapter implements OrderCreationPort {

    private final OrderCreatedEventPublisher publisher;
    private final OrderPersistenceService orderPersistenceService;

    @Transactional
    @Override
    public void createOrder(Order order, OrderCreatedEvent event) {
        orderPersistenceService.persist(order);
        publisher.publish(event);
    }
}

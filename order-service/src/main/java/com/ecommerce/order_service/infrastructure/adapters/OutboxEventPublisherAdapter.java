package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.application.port.EventPublisherPort;
import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.service.JpaOrderAdapter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class OutboxEventPublisherAdapter implements EventPublisherPort {
     
    private final OrderCreatedEventPublisher publisher;
    private final JpaOrderAdapter jpaAdapter;
    
    @Transactional
    @Override
    public void execute(Order order, OrderCreatedEvent event) {
        jpaAdapter.persist(order);
        publisher.publish(event);
    }
}

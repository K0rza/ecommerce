package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.JpaOrderAdapter;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutbox;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventPublisherAdapter implements OrderCreatedEventPublisher {

    private final JpaOrderAdapter jpaAdapter;

    @Override
    public void publish(OrderCreatedEvent event) {
        OrderOutbox dto = OrderOutbox.to(event);
        
        jpaAdapter.save(dto);
    }

}

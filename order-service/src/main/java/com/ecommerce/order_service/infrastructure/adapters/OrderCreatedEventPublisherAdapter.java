package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.adapter.JpaOrderOutboxTableAdapter;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxTable;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventPublisherAdapter implements OrderCreatedEventPublisher {

    private final JpaOrderOutboxTableAdapter jpaAdapter;

    @Override
    public void publish(OrderCreatedEvent event) {
        OrderOutboxTable dto = OrderOutboxTable.to(event);
        
        jpaAdapter.save(dto);
    }

}

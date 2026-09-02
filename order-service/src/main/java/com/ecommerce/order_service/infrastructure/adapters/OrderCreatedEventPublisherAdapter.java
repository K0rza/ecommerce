package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.adapter.OrderOutboxJpaRepository;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderCreatedEventPublisherAdapter implements OrderCreatedEventPublisher {

    private final OrderOutboxJpaRepository orderOutboxRepository;

    @Override
    public void publish(OrderCreatedEvent event) {
        OrderOutboxDto dto = OrderOutboxDto.to(event);

        orderOutboxRepository.save(dto);
    }

}

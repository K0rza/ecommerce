package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.adapter.OrderOutboxJpaRepository;
import com.ecommerce.order_service.infrastructure.persistence.dto.OrderOutboxDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventPublisherAdapter implements OrderCreatedEventPublisher {

    private final OrderOutboxJpaRepository orderOutboxRepository;

    @Override
    public void publish(OrderCreatedEvent event) {
        log.info("%s::publish begins.".formatted(this.getClass().getSimpleName()));
        
        OrderOutboxDto dto = OrderOutboxDto.to(event);
        
        orderOutboxRepository.save(dto);
        log.debug("%s::save dto to outbox table to publish.".formatted(this.getClass().getSimpleName(), dto));
        
        log.info("%s::publish ends.".formatted(this.getClass().getSimpleName()));
    }

}

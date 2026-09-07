package com.ecommerce.order_service.infrastructure.adapters;

import org.springframework.stereotype.Service;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.application.port.OrderCreationPort;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.service.OrderPersistenceService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class OrderCreationAdapter implements OrderCreationPort {

    private final OrderCreatedEventPublisher publisher;
    private final OrderPersistenceService orderPersistenceService;

    @Transactional
    @Override
    public void createOrder(Order order, OrderCreatedEvent event) {
        log.info("%s::createOrder begins.".formatted(this.getClass().getSimpleName()));

        orderPersistenceService.persist(order);
        log.debug("%s::createOrder persist the order completed. %s".formatted(this.getClass().getSimpleName(), order));

        publisher.publish(event);
        log.debug("%s::createOrder publish the event completed. %s".formatted(this.getClass().getSimpleName(), event));

        log.info("%s::createOrder ends.".formatted(this.getClass().getSimpleName()));
    }
}

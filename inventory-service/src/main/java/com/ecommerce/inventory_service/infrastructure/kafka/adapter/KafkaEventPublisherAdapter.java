package com.ecommerce.inventory_service.infrastructure.kafka.adapter;

import org.springframework.stereotype.Service;

import com.ecommerce.inventory_service.application.port.OrderStatusPublisherPort;
import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.infrastructure.kafka.event.writer.OrderEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaEventPublisherAdapter implements OrderStatusPublisherPort {
    private final OrderEventPublisher publisher;
    
    @Override
    public void orderOutOfStock(int orderId) {
        publisher.publishOutOfStock(orderId);
    }

    @Override
    public void publisCreatedSuccessfulyEvent(Inventory inventory) {
        publisher.publishOrderCreated(inventory.getOrderId());
    }

}

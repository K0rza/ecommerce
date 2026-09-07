package com.ecommerce.inventory_service.infrastructure.kafka.event.writer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishOrderCreated(int orderId) {
       kafkaTemplate.send("order-created-successfully", Integer.toString(orderId));
    }

    public void publishOutOfStock(int orderId) {
       kafkaTemplate.send("order-out-of-stock", Integer.toString(orderId));
    }

}

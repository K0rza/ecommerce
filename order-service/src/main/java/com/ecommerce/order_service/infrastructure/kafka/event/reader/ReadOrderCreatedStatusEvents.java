package com.ecommerce.order_service.infrastructure.kafka.event.reader;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.usecases.OrderOutOfStockUseCase;
import com.ecommerce.order_service.application.usecases.OrderSuccessfullyCreatedUseCase;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReadOrderCreatedStatusEvents {

    private final OrderOutOfStockUseCase orderOutOfStockUseCase;
    private final OrderSuccessfullyCreatedUseCase orderSuccessfullyCreatedUseCase;

    @KafkaListener(topics = "order-created-successfully")
    public void readOrderSuccessfullyCreatedEvent(ConsumerRecord<String, String> event) {
        int orderId = Integer.valueOf(event.value());

        orderSuccessfullyCreatedUseCase.execute(orderId);
    }

    @KafkaListener(topics = "order-out-of-stock")
    public void readOrderOutOfStockEvent(ConsumerRecord<String, String> event) {
        int orderId = Integer.valueOf(event.value());

        orderOutOfStockUseCase.execute(orderId);
    }

}

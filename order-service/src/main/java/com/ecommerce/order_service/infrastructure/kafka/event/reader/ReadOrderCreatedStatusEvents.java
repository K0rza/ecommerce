package com.ecommerce.order_service.infrastructure.kafka.event.reader;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.order_service.application.usecases.OrderOutOfStockUseCase;
import com.ecommerce.order_service.application.usecases.OrderSuccessfullyCreatedUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReadOrderCreatedStatusEvents {

    private final OrderOutOfStockUseCase orderOutOfStockUseCase;
    private final OrderSuccessfullyCreatedUseCase orderSuccessfullyCreatedUseCase;

    @KafkaListener(topics = "order-created-successfully")
    public void readOrderSuccessfullyCreatedEvent(ConsumerRecord<String, String> event) {
        int orderId = Integer.valueOf(event.value());
        log.debug("%s::read order-created-successfully orderId: %s.".formatted(this.getClass().getSimpleName(), orderId));
        
        orderSuccessfullyCreatedUseCase.execute(orderId);
    }
    
    @KafkaListener(topics = "order-out-of-stock")
    public void readOrderOutOfStockEvent(ConsumerRecord<String, String> event) {
        int orderId = Integer.valueOf(event.value());
        log.debug("%s::read order-out-of-stock orderId: %s.".formatted(this.getClass().getSimpleName(), orderId));
        
        orderOutOfStockUseCase.execute(orderId);
    }

}

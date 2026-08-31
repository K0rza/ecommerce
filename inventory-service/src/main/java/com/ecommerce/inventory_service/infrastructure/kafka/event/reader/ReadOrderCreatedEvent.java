package com.ecommerce.inventory_service.infrastructure.kafka.event.reader;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.usecase.OrderCreatedUseCase;
import com.ecommerce.inventory_service.domain.value.OrderRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ReadOrderCreatedEvent {

    private final OrderCreatedUseCase useCase;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "order-created")
    public void read(ConsumerRecord<String, String> record) {
        try {
            OrderRecord orderEvent = mapper.readValue(record.value(), OrderRecord.class);
            useCase.process(orderEvent);
        } catch (Exception e) {
            System.err.print(e);
        }        
    }
}

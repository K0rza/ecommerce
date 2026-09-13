package com.ecommerce.inventory_service.infrastructure.kafka.event.reader;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.port.in.ProcessOrderUseCase;
import com.ecommerce.inventory_service.domain.value.OrderRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Slf4j 
@Component
public class ReadOrderCreatedEvent {

    private final ProcessOrderUseCase useCase;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "order-created")
    public void read(ConsumerRecord<String, String> record) {
        try {
            OrderRecord orderEvent = mapper.readValue(record.value(), OrderRecord.class);
            useCase.process(orderEvent);
        } catch (Exception e) {
            log.error("Cannot map record to OrderRecord", e);
        }        
    }
}

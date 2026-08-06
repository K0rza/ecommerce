package com.ecommerce.inventory_service.infrastructure.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.usecase.ProductCreateUseCase;
import com.ecommerce.inventory_service.infrastructure.kafka.entity.ProductEventType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Component
public class ProductCreatedEventConsumer {

    private ObjectMapper mapper;
    private ProductCreateUseCase useCase;

    @KafkaListener(topics = "PRODUCT-CREATED-EVENTS")
    public void consume(ConsumerRecord<String, String> record) {
        String payload = record.value();
        try {
            ProductEventType productEvent = mapper.readValue(payload, ProductEventType.class);
            JsonNode node = mapper.readTree(productEvent.getPayload());
            
            useCase.createProduct(productEvent.toDomain(node.get("amount").asInt()));
        } catch (JsonProcessingException e) {
            log.error("Couldnt parse incoming kafka data to product");
        }

    }


}

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

    private final ObjectMapper mapper;
    private final ProductCreateUseCase useCase;

    @KafkaListener(topics = "PRODUCT-CREATED-EVENTS")
    public void consume(ConsumerRecord<String, String> record) {
        log.error("Product-Created-Events consumed.");

        String payload = record.value();
        try {
            ProductEventType productEvent = mapper.readValue(payload, ProductEventType.class);
            log.error("consumed event: " + productEvent);

            JsonNode node = mapper.readTree(productEvent.getPayload());

            log.error("ProductCreatedUseCase is calling... amount: " + node.get("amount").asInt());
            useCase.createProduct(productEvent.toDomain(node.get("amount").asInt()));
            log.error("ProductCreatedUseCase is called.");

        } catch (Exception e) {
            log.error("exception " + e);
        }

    }


}

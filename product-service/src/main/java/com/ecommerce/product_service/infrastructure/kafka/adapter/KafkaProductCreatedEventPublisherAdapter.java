package com.ecommerce.product_service.infrastructure.kafka.adapter;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.domain.port.ProductEventPublisher;
import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaProductCreatedEventPublisherAdapter implements ProductEventPublisher {

    private final KafkaTemplate<String, ProductCreatedEvent> KafkaTemplate;
    private final ObjectMapper mapper;

    @Override
    public void publish(ProductCreatedEvent event) {
        /*
        KafkaTemplate.send("product-created-events", event.title(), event).whenComplete((res, ex) -> {
            if(ex!=null) 
                log.error("Message cannot be sent to kafka broker. %s", ex);
            else 
                log.error("Message sent succesfully, offset: %s", res.getRecordMetadata().offset());

        });
        */
        //OutboxEventEntity outboxEntity = OutboxEventEntity.to(event.productId(), "product-created-events", eventPayload);
        String payload;
        try {
            payload = mapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            payload = "CANNOT CONVERT INTO JSON";
        }
        OutboxEventEntity entity = OutboxEventEntity.of(event.productId(), "PRODUCT-CREATED-EVENTS", payload);
    }
}

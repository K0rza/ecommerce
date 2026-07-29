package com.ecommerce.product_service.infrastructure.kafka.adapter;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.domain.port.ProductEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class KafkaProductCreatedEventPublisherAdapter implements ProductEventPublisher {

    private final KafkaTemplate<String, ProductCreatedEvent> KafkaTemplate;

    @Override
    public void publish(ProductCreatedEvent event) {
        KafkaTemplate.send("product-created-events", event.title(), event).whenComplete((res, ex) -> {
            if(ex!=null) 
                log.error("Message cannot be sent to kafka broker. %s", ex);
            else 
                log.error("Message sent succesfully, offset: %s", res.getRecordMetadata().offset());

        });
    }
}

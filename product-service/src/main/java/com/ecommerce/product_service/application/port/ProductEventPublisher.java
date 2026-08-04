package com.ecommerce.product_service.application.port;

import java.util.UUID;
import java.util.stream.Stream;

import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;

public interface ProductEventPublisher {

    void publish(ProductCreatedEvent event);

    void processed(UUID id);

    Stream<OutboxEventEntity> findNotProcessed();
}

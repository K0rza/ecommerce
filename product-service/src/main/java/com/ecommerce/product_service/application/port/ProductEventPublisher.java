package com.ecommerce.product_service.application.port;

import java.util.stream.Stream;

import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.infrastructure.persistence.entity.OutboxEventEntity;

public interface ProductEventPublisher {

    void publish(ProductCreatedEvent event);

    void processed(int id);

    Stream<OutboxEventEntity> findNotProcessed();
}

package com.ecommerce.product_service.domain.port;

import com.ecommerce.product_service.domain.event.ProductCreatedEvent;

public interface ProductEventPublisher {

    void publish(ProductCreatedEvent event);
}

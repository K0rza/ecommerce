package com.ecommerce.order_service.application.port;

import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public interface OrderCreatedEventPublisher {

    void publish(OrderCreatedEvent event);
}

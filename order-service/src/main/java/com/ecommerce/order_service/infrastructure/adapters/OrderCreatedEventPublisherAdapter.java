package com.ecommerce.order_service.infrastructure.adapters;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public class OrderCreatedEventPublisherAdapter implements OrderCreatedEventPublisher {

    @Override
    public void publish(OrderCreatedEvent event) {
        
        //TODO: send the event over kafka to inventory-service
    }

}

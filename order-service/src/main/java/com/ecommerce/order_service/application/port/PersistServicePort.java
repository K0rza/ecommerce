package com.ecommerce.order_service.application.port;

import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public interface PersistServicePort {

    void execute(Order order, OrderCreatedEvent event);

}

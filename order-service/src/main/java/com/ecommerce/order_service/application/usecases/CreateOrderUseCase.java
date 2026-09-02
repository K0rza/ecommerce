package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.EventPublisherPort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.entity.OrderRequest;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public class CreateOrderUseCase {
    
    private final EventPublisherPort persistService;

    public CreateOrderUseCase(EventPublisherPort eventPublisher) {
        this.persistService = eventPublisher;
    }

    public void process(OrderRequest request) {

        Order order = Order.fromRequest(request, ORDER_STATUS.PENDING);

        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getProductId(), order.getQuantity(), order.getStatus());

        persistService.execute(order, event);
    }

}

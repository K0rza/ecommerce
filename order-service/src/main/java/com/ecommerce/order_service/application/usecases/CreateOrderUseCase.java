package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.entity.OrderRequest;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;
import com.ecommerce.order_service.infrastructure.persistence.service.DatabaseService;

public class CreateOrderUseCase {
    private final OrderCreatedEventPublisher publisher;
    private final DatabaseService databaseService;

    public CreateOrderUseCase(OrderCreatedEventPublisher publisher, DatabaseService databaseService) {
        this.publisher = publisher;
        this.databaseService = databaseService;
    }

    public void execute(OrderRequest request) {

        Order order = Order.fromRequest(request, ORDER_STATUS.PENDING);

        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getProductId(), order.getQuantity(), order.getStatus());

        databaseService.persist(order);
        publisher.publish(event);
    }

}

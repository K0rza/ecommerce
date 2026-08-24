package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.OrderCreatedEventPublisher;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public class CreateOrderUseCase {
    private OrderCreatedEventPublisher publisher;

    public CreateOrderUseCase(OrderCreatedEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void execute() {
        int orderId = 10;
        String customerId = "TEST";
        int productId = 1903;
        int quantity = 5;
        
        Order order = new Order(orderId, customerId, productId, quantity, ORDER_STATUS.PENDING);

        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getProductId(), order.getQuantity(), order.getStatus());

        publisher.publish(event);
    }

}

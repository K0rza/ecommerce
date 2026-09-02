package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.OrderCreationPort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.entity.OrderRequest;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public class CreateOrderUseCase {

    private final OrderCreationPort orderCreationPort;

    public CreateOrderUseCase(OrderCreationPort orderCreationPort) {
        this.orderCreationPort = orderCreationPort;
    }

    public void process(OrderRequest request) {

        Order order = Order.fromRequest(request, ORDER_STATUS.PENDING);

        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getProductId(), order.getQuantity(), order.getStatus());

        orderCreationPort.createOrder(order, event);
    }

}

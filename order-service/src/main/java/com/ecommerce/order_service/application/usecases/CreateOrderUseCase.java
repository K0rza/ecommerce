package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.Logger;
import com.ecommerce.order_service.application.port.OrderCreationPort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;
import com.ecommerce.order_service.domain.entity.Order;
import com.ecommerce.order_service.domain.entity.OrderRequest;
import com.ecommerce.order_service.domain.event.OrderCreatedEvent;

public class CreateOrderUseCase {

    private final OrderCreationPort orderCreationPort;
    private final Logger logger;

    public CreateOrderUseCase(OrderCreationPort orderCreationPort, Logger logger) {
        this.orderCreationPort = orderCreationPort;
        this.logger = logger;
    }

    public void process(OrderRequest request) {
        logger.info("%s::process begins.".formatted(this.getClass().getSimpleName()));

        Order order = Order.fromRequest(request, ORDER_STATUS.PENDING);
        
        OrderCreatedEvent event = new OrderCreatedEvent(order.getOrderId(), order.getProductId(), order.getQuantity(), order.getStatus());

        logger.debug("%s::process event created. %s".formatted(this.getClass().getSimpleName(), event)); 

        orderCreationPort.createOrder(order, event);

        logger.info("%s::process ends.".formatted(this.getClass().getSimpleName()));   
    }
}

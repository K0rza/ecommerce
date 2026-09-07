package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.Logger;
import com.ecommerce.order_service.application.port.OrderStatusUpdatePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public class OrderOutOfStockUseCase {

    private final OrderStatusUpdatePort orderStatusUpdatePort;
    private final Logger logger;

    public OrderOutOfStockUseCase(OrderStatusUpdatePort orderStatusUpdatePort, Logger logger) {
        this.orderStatusUpdatePort = orderStatusUpdatePort;
        this.logger = logger;
    }

    public void execute(int orderId) {
        logger.info("%s::execute begins.".formatted(this.getClass().getSimpleName()));

        orderStatusUpdatePort.updateStatus(orderId, ORDER_STATUS.CANCELED);
        logger.debug("%s::execute update the order status CANCELED, orderId: %s.".formatted(this.getClass().getSimpleName(), orderId));
        
        logger.info("%s::execute ends.".formatted(this.getClass().getSimpleName()));
    }
}

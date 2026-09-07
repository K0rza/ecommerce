package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.Logger;
import com.ecommerce.order_service.application.port.OrderStatusUpdatePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public class OrderSuccessfullyCreatedUseCase {

    private final OrderStatusUpdatePort orderStatusUpdatePort;
    private final Logger logger;

    public OrderSuccessfullyCreatedUseCase(OrderStatusUpdatePort orderStatusUpdatePort, Logger logger) {
        this.orderStatusUpdatePort = orderStatusUpdatePort;
        this.logger = logger;
    }

    public void execute(int orderId) {
        logger.info("%s::execute begins.".formatted(this.getClass().getSimpleName()));
        
        orderStatusUpdatePort.updateStatus(orderId, ORDER_STATUS.COMPLETED);
        logger.info("%s::execute updateStatus to COMPLETED, orderId: %s".formatted(this.getClass().getSimpleName(), orderId));

        logger.info("%s::execute ends.".formatted(this.getClass().getSimpleName()));
    }

}

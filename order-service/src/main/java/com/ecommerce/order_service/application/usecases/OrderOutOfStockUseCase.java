package com.ecommerce.order_service.application.usecases;

import com.ecommerce.order_service.application.port.OrderStatusUpdatePort;
import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public class OrderOutOfStockUseCase {

    private final OrderStatusUpdatePort orderStatusUpdatePort;

    public OrderOutOfStockUseCase(OrderStatusUpdatePort orderStatusUpdatePort) {
        this.orderStatusUpdatePort = orderStatusUpdatePort;
    }

    public void execute(int orderId) {
        orderStatusUpdatePort.updateStatus(orderId, ORDER_STATUS.CANCELED);
    }
}

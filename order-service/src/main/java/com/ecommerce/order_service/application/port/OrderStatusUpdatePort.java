package com.ecommerce.order_service.application.port;

import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public interface OrderStatusUpdatePort {
    void updateStatus(int orderId, ORDER_STATUS status);
}

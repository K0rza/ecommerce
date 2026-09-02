package com.ecommerce.order_service.application.port;

import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public interface OrderPersistentPort {
    void orderStatus(int orderId, ORDER_STATUS orderStatus);
}

package com.ecommerce.order_service.domain.event;

import com.ecommerce.order_service.domain.entity.ORDER_STATUS;

public record OrderCreatedEvent(int orderId, int productId, int quantity, ORDER_STATUS orderStatus) {
}

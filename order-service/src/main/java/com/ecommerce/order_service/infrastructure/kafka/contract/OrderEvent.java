package com.ecommerce.order_service.infrastructure.kafka.contract;

public record OrderEvent(int eventId, int orderId, int productId, int quantity) {}

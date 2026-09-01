package com.ecommerce.inventory_service.domain.value;

public record OrderRecord(int orderId, int productId, int quantity) {

    public OrderRecord {
        if(orderId < 0 || productId < 0 || quantity < 0)
            throw new IllegalArgumentException();
    };
}

package com.ecommerce.inventory_service.domain.value;

import com.ecommerce.inventory_service.domain.type.ORDER_STATUS;

public record OrderRecord(int orderId,
    int productId,
    int quantity, 
    ORDER_STATUS status,
    boolean published) {

    public OrderRecord {
        if(orderId < 0 || productId < 0 || quantity < 0)
            throw new IllegalArgumentException();
    };
}

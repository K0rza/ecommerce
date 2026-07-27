package com.ecommerce.product_service.domain.event;

import com.ecommerce.product_service.domain.model.Sku;

public record ProductCreatedEvent(int productId, Sku sku, String title) {
    
    public ProductCreatedEvent {
        if(productId < 0) throw new IllegalArgumentException("ProductId cannot be negative");
        if(sku == null) throw new IllegalArgumentException("The sku object is null.");
        if(title == null ||title.isBlank()) throw new IllegalArgumentException("The title is failed.");
    }

}

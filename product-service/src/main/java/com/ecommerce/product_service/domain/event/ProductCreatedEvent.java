package com.ecommerce.product_service.domain.event;

import com.ecommerce.product_service.domain.model.Product;

public record ProductCreatedEvent(int productId, int amount) {
    
    public ProductCreatedEvent {
        if(productId < 0) throw new IllegalArgumentException("ProductId cannot be negative");
        if(amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
    }

    public static ProductCreatedEvent of(Product product) {
        return new ProductCreatedEvent(product.getProductId(), product.getStock());
    }

}

package com.ecommerce.product_service.domain.event;

public record ProductCreatedEvent(int productId, int amount) {
    
    public ProductCreatedEvent {
        if(productId < 0) throw new IllegalArgumentException("ProductId cannot be negative");
        if(amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
    }

}

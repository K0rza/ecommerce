package com.ecommerce.inventory_service.infrastructure.kafka.entity;

import com.ecommerce.inventory_service.domain.entity.Product;

public class ProductEventType {
    private int id;
    private int productId;
    private String eventName;
    private String payload;
    private boolean processed;

    public ProductEventType(int id, int productId, String eventName, String payload, boolean processed) {
        this.id = id;
        this.productId = productId;
        this.eventName = eventName;
        this.payload = payload;
        this.processed = processed;
    }

    public int getId() { return id; }

    public int getProductId() { return productId; }

    public String getEventName() { return eventName; }

    public String getPayload() { return payload; }

    public boolean isProcessed() { return processed; }

    public Product toDomain(int stock) {
        return new Product(productId, id, stock);
    }
}

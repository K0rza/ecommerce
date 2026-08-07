package com.ecommerce.inventory_service.infrastructure.kafka.entity;

import com.ecommerce.inventory_service.domain.entity.Product;

public class ProductEventType {
    private int eventId;
    private int productId;
    private String eventName;
    private String payload;
    private boolean processed;

    public ProductEventType() {}

    public ProductEventType(int eventId, int productId, String eventName, String payload, boolean processed) {
        this.eventId = eventId;
        this.productId = productId;
        this.eventName = eventName;
        this.payload = payload;
        this.processed = processed;
    }

    public int getEventId() { return eventId; }

    public int getProductId() { return productId; }

    public String getEventName() { return eventName; }

    public String getPayload() { return payload; }

    public boolean isProcessed() { return processed; }

    public Product toDomain(int stock) {
        return new Product(productId, eventId, stock);
    }

    @Override
    public String toString() {
        return "ProductEventType [id=" + eventId + ", productId=" + productId + ", eventName=" + eventName + ", payload="
                + payload + ", processed=" + processed + "]";
    }

    public record ProductEventPayload (int productId, int amount) {}

}

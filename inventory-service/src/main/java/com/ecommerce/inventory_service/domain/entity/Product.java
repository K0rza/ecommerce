package com.ecommerce.inventory_service.domain.entity;

public class Product {
    private int productId;
    private int eventId;
    private int stock;

    public Product(int productId, int eventId, int stock) {
        this.productId = productId;
        this.eventId = eventId;
        this.stock = stock;
    }

    public int getProductId() { return productId; }

    public int getEventId() { return eventId; }

    public int getStock() { return stock; }
}

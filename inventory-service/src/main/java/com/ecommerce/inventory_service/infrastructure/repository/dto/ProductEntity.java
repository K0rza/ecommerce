package com.ecommerce.inventory_service.infrastructure.repository.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    private int productId;
    private int eventId;
    private int stock;

    public int getProductId() { return productId; }

    public int getEventId() { return eventId; }

    public int getStock() { return stock; }
}

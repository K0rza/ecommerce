package com.ecommerce.inventory_service.infrastructure.repository.dto;

import com.ecommerce.inventory_service.domain.entity.Product;

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

    public ProductEntity() {}

    public ProductEntity(int productId, int eventId, int stock) {
        this.productId = productId;
        this.eventId = eventId;
        this.stock = stock;
    }

    public int getProductId() { return productId; }

    public int getEventId() { return eventId; }

    public int getStock() { return stock; }

    public static ProductEntity fromDomain(Product product) {
        return new ProductEntity(product.getProductId(), product.getEventId(), product.getStock());
    }
}

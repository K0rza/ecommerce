package com.ecommerce.inventory_service.infrastructure.repository.dto;

import com.ecommerce.inventory_service.domain.entity.Inventory;
import com.ecommerce.inventory_service.domain.entity.Product;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class ProductDto {

    public static final int UNUSED_EVENT_ID = 0;

    @Id
    private int productId;
    private int eventId;
    private int stock;

    public ProductDto() {}

    public ProductDto(int productId, int eventId, int stock) {
        this.productId = productId;
        this.eventId = eventId;
        this.stock = stock;
    }

    public int getProductId() { return productId; }

    public int getEventId() { return eventId; }

    public int getStock() { return stock; }

    public static ProductDto from(Product product) {
        return new ProductDto(product.getProductId(), product.getEventId(), product.getStock());
    }

    public static ProductDto from(Inventory inventory) {
        return new ProductDto(inventory.getProductId(), UNUSED_EVENT_ID, inventory.getStock());
    }
}

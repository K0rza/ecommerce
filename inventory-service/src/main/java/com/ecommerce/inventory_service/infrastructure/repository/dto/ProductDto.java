package com.ecommerce.inventory_service.infrastructure.repository.dto;

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

    private ProductDto(int productId, int eventId, int stock) {
        this.productId = productId;
        this.eventId = eventId;
        this.stock = stock;
    }

    public static ProductDto from(Product product) {
        return new ProductDto(product.getProductId(), product.getEventId(), product.getStock());
    }

    public static ProductDto from(int productId, int stock ) {
        return new ProductDto(productId, UNUSED_EVENT_ID, stock);
    }

    public int getProductId() { return productId; }

    public int getEventId() { return eventId; }

    public int getStock() { return stock; }

    public void updateStock(int stock) { this.stock = stock; }
}

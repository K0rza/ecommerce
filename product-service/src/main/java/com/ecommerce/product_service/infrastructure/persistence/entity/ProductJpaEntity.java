package com.ecommerce.product_service.infrastructure.persistence.entity;

import java.math.BigDecimal;

import com.ecommerce.product_service.domain.model.Price;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.model.Sku;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class ProductJpaEntity {

    @Id
    @Column(name = "productId")
    private int productId;

    private String title;
    private String sku;
    private BigDecimal priceAmount;
    private String priceCurreny;
    
    private int stock;

    public ProductJpaEntity() {}

    public static ProductJpaEntity fromDomain(Product product) {
        ProductJpaEntity entity = new ProductJpaEntity();
        entity.productId = product.getProductId();
        entity.priceAmount = product.getPrice().amount();
        entity.priceCurreny = product.getPrice().currency();
        entity.sku = product.getSku().toString();
        entity.stock = product.getStock();
        entity.title = product.getTitle();

        return entity;
    }

    public Product toDomain() {
        return new Product(
            this.productId,
            new Price(priceAmount, priceCurreny),
            new Sku(this.sku),
            this.title,
            this.stock
        );
    }
}

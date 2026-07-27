package com.ecommerce.product_service.domain.model;

import java.util.Optional;

import com.ecommerce.product_service.domain.exception.InvalidVersionException;

public class Product {
    private int productId;
    private Price price;
    private Sku sku;
    private String title;
    private int stock;
    private long version;

    public Product(int productId, Price price, Sku sku, String title, int stock, long version) {

        if(productId < 0) throw new IllegalArgumentException("ProductId cannot be negative");
        if(price == null ) throw new IllegalArgumentException("The price object is null.");
        if(sku == null) throw new IllegalArgumentException("The sku object is null.");
        if(title == null ||title.isBlank()) throw new IllegalArgumentException("The title is failed.");
        if(stock <= 0) throw new IllegalArgumentException("The product is stock-out.");
        if(version <= 0) throw new IllegalArgumentException("THe version must bu higher than 0");
        if(version < this.version) throw new InvalidVersionException(this.version);

        this.productId = productId;
        this.price = price;
        this.sku = sku;
        this.title = title;
        this.stock = stock;
        this.version = version;
    }

    public void decreaseStock(int quantity) {
        if(quantity < 0) throw new IllegalArgumentException("Cannot decrease the stock by " + quantity);

        if(stock < quantity) throw new IllegalStateException("No such item found in stock. Stock count: " + stock);

        stock -= quantity;
    }

    public void updatePrice(Price newPrice) {
        Optional.ofNullable(newPrice).ifPresentOrElse(
            x -> this.price = x, 
            () -> {throw new IllegalArgumentException("New price is null");});
    }

    public int getProductId() { return productId; }
    public Price getPrice() { return price; }
    public Sku getSku() { return sku; }
    public String getTitle() { return title; }
    public int getStock() { return stock; }
    public long getVersion() { return version; }
}

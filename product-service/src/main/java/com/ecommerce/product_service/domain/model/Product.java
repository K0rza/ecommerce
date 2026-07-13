package com.ecommerce.product_service.domain.model;

import java.util.Optional;

public class Product {
    private int productId;
    private Price price;
    private Sku sku;
    private String title;
    private int stock;

    public Product(int productId, Price price, Sku sku, String title, int stock) {

        if(productId < 0) throw new IllegalArgumentException("ProductId cannot be negative");
        if(price == null ) throw new IllegalArgumentException("The price object is null.");
        if(sku == null) throw new IllegalArgumentException("The sku object is null.");
        if(title == null ||title.isBlank()) throw new IllegalArgumentException("The title is failed.");
        if(stock <= 0) throw new IllegalArgumentException("The product is stock-out.");

        this.productId = productId;
        this.price = price;
        this.sku = sku;
        this.title = title;
        this.stock = stock;
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
}

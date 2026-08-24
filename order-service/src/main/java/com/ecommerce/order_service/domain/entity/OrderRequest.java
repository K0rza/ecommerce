package com.ecommerce.order_service.domain.entity;

public class OrderRequest {

    private int orderId;
    private String customerId;
    private int productId;
    private int quantity;
    public OrderRequest(int orderId, String customerId, int productId, int quantity) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
    }
    public int getOrderId() {
        return orderId;
    }
    public String getCustomerId() {
        return customerId;
    }
    public int getProductId() {
        return productId;
    }
    public int getQuantity() {
        return quantity;
    }

    
}

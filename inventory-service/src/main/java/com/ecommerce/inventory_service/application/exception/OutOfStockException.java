package com.ecommerce.inventory_service.application.exception;

public class OutOfStockException extends RuntimeException {

    private int orderId;

    public OutOfStockException(int orderId) {
        super("OutOfStock for orderId : " + orderId);
        this.orderId = orderId;
    }

    public int getOrderId() { return orderId; }
}

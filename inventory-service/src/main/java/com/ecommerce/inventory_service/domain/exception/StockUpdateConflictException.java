package com.ecommerce.inventory_service.domain.exception;

public class StockUpdateConflictException extends RuntimeException {

    private int productId;

    public StockUpdateConflictException(int productId) {
        super("Concurrent stock update conflict for productId : " + productId);
        this.productId = productId;
    }

    public int getProductId() { return productId; }
}

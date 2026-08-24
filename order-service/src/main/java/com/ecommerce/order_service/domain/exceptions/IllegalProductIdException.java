package com.ecommerce.order_service.domain.exceptions;

public class IllegalProductIdException extends RuntimeException {

    private int productId = 0;

    public IllegalProductIdException(int productId) {
        super("The Product ID is illegel. Must be higher than 0");
        this.productId = productId;
    }

    public int getProductId() { return this.productId; }
}

package com.ecommerce.order_service.domain.exceptions;

public class IllegalQuantityException extends RuntimeException {

    private int quantity = 0;

    public IllegalQuantityException(int quantity) {
        super("The Quantity Argument is illegel. Must be higher than 0");
        this.quantity = quantity;
    }

    public int getQuantity() { return this.quantity; }
}

package com.ecommerce.order_service.domain.exceptions;

public class IllegalOrderIdException extends RuntimeException {

    private int orderId = 0;

    public IllegalOrderIdException(int customerId) {
        super("The Order ID is illegel. Must be higher than 0");
        this.orderId = customerId;
    }

    public int getOrderId() { return this.orderId; }
}

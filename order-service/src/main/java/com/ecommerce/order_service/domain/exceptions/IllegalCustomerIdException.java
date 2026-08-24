package com.ecommerce.order_service.domain.exceptions;

public class IllegalCustomerIdException extends RuntimeException {

    private String customerId;

    public IllegalCustomerIdException(String customerId) {
        super("The Customer ID is empty");
        this.customerId = customerId;
    }

    public String getCustomerId() { return this.customerId; }

}

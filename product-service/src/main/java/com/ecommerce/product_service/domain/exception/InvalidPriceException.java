package com.ecommerce.product_service.domain.exception;

public class InvalidPriceException extends DomainException {

    private int value;
    public InvalidPriceException(int value) {
        super(String.format("The price is invalid %s. The price must be higher than 0", value));
        this.value = value;
    }

    public int getInvalidValue() { return this.value; }

}

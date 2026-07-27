package com.ecommerce.product_service.domain.exception;

public class InvalidVersionException extends RuntimeException {

    public InvalidVersionException(long version) {
        super(String.format("The version must be higher than 0 and the previous one, previous version: %s", version));
    }
}

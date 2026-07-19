package com.ecommerce.product_service.domain.exception;

public class InvalidSkuException extends DomainException {

    public InvalidSkuException(String sku) {
        super(String.format("Stock code is invalid (%s). Example: PRD-12345", sku));
    }
}

package com.ecommerce.product_service.domain.model;

public record Sku (String value) {

    public Sku {
        if(value.isBlank() || value.matches("^[A-Z0-9]{8,12}$"))
            throw new IllegalArgumentException("Stock code is invalid. Example: PRD-12345");

    };
}

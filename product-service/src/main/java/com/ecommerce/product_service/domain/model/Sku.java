package com.ecommerce.product_service.domain.model;

import com.ecommerce.product_service.domain.exception.InvalidSkuException;

public record Sku (String value) {

    public Sku {
        if(value.isBlank() || value.matches("^[A-Z0-9]{8,12}$"))
            throw new InvalidSkuException(value);
    };
}

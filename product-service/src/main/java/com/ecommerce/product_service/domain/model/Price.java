package com.ecommerce.product_service.domain.model;

import java.math.BigDecimal;
import java.util.Optional;

public record Price (BigDecimal amount, String currency) {
    public Price {
        Optional.ofNullable(amount).ifPresentOrElse(
            this::isAmountNegative, 
            () -> {throw new IllegalArgumentException("The amount is null");}
        );

        if(currency.isBlank()) {
            throw new IllegalArgumentException("Currency is blank.");
        }
    }

    private void isAmountNegative(BigDecimal amount) {
        if(amount.intValue() < 0)
            throw new IllegalArgumentException("The Amount cannot be negative");
    }
}

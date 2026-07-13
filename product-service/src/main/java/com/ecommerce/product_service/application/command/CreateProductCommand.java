package com.ecommerce.product_service.application.command;

import java.math.BigDecimal;

public record CreateProductCommand (
    String title,
    String sku,
    BigDecimal price,
    String currency,
    int stock
) {}

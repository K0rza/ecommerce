package com.ecommerce.inventory_service.infrastructure.adapter;

import org.springframework.stereotype.Component;

import com.ecommerce.inventory_service.application.port.InventoryPort;
import com.ecommerce.inventory_service.application.usecase.ProductStock;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class InventoryAdapter implements InventoryPort {

    private final ProductStock productStock;
    
    @Override
    public int getProductStock(String sku) {
        return productStock.getProductStock(sku);
    }
}

package com.ecommerce.inventory_service.infrastructure.adapter;

import org.springframework.stereotype.Repository;

import com.ecommerce.inventory_service.application.port.DatabaseServicePort;
import com.ecommerce.inventory_service.infrastructure.repository.JpaInventoryAdapter;
import com.ecommerce.inventory_service.infrastructure.repository.dto.InventoryDto;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DatabaseServiceAdapter implements DatabaseServicePort {

    private final JpaInventoryAdapter jpaAdapter;

    @Override
    public int getStock(int productId) {
        return jpaAdapter
            .findById(productId)
            .map(InventoryDto::getStock)
            .orElse(0);
    }
}

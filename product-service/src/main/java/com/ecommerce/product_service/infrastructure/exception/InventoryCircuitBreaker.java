package com.ecommerce.product_service.infrastructure.exception;

import org.springframework.stereotype.Service;

import com.ecommerce.product_service.infrastructure.httpClient.InventoryClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class InventoryCircuitBreaker {

    private final InventoryClient inventoryClient;

    @CircuitBreaker(name = "inventoryCB", fallbackMethod = "fallback")
    public Integer checkStock(String sku) {
        log.info("{} product stock is queing from inventory-service", sku);
        return inventoryClient.getStock(sku);
    }

    public Integer fallback() {
        log.error("Inventory Service is unavailable now, please try again later.");
        return 0;
    }
}

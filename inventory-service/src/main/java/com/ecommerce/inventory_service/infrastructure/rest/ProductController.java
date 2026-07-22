package com.ecommerce.inventory_service.infrastructure.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.inventory_service.infrastructure.adapter.InventoryAdapter;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final InventoryAdapter inventoryAdapter;

    @GetMapping("/inventory/{sku}")
    public int getProductStock(@RequestParam String sku) {
        System.out.println("get Proudct for " + sku);
        return inventoryAdapter.getProductStock(sku);
    }
}

package com.ecommerce.product_service.infrastructure.httpClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/inventory/{sku}")
    public int getStock(@PathVariable("sku") String sku);
}

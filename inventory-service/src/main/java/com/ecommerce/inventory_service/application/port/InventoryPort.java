package com.ecommerce.inventory_service.application.port;

public interface InventoryPort {

    int getProductStock(String sku);
}

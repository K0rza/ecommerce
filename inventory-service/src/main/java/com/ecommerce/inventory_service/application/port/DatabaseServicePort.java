package com.ecommerce.inventory_service.application.port;

public interface DatabaseServicePort {

    int getStock(int productId);

}

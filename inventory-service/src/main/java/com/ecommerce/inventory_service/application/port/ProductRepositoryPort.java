package com.ecommerce.inventory_service.application.port;

import java.util.function.Consumer;

import com.ecommerce.inventory_service.domain.entity.Product;

public interface ProductRepositoryPort {

    void ifNewProductOrElse(Product product, Consumer<Product> createNewProductJob, Consumer<Integer> rejectProductJob);

    void create(Product product);
    
    int getStock(int productId);

    void updateStock(int productId, int stock);

}

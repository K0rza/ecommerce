package com.ecommerce.inventory_service.domain.port;

import java.util.function.Consumer;

import com.ecommerce.inventory_service.domain.entity.Product;

public interface RepositoryPort {

    void ifNewProductOrElse(Product product, Consumer<Product> createNewProductJob, Runnable rejectProductJob);

}

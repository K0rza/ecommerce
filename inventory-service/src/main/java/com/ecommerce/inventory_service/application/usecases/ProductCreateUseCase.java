package com.ecommerce.inventory_service.application.usecases;

import com.ecommerce.inventory_service.application.port.out.ProductRepositoryPort;
import com.ecommerce.inventory_service.domain.entity.Product;

public class ProductCreateUseCase {

    private ProductRepositoryPort repo;

    public ProductCreateUseCase(ProductRepositoryPort repo) {
        this.repo = repo;
    }

    public void createProduct(Product product) {
        repo.ifNewProductOrElse(product, this::createNewProduct, this::rejectProduct);
    }

    private void createNewProduct(Product product) {        
        repo.create(product);
    }

    private void rejectProduct(int eventId) {
        //throw new IllegalEventIdempotent(eventId);
    }

}

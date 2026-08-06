package com.ecommerce.inventory_service.application.usecase;

import com.ecommerce.inventory_service.domain.entity.Product;
import com.ecommerce.inventory_service.domain.port.RepositoryPort;

public class ProductCreateUseCase {

    private RepositoryPort repo;

    public ProductCreateUseCase(RepositoryPort repo) {
        this.repo = repo;
    }

    public void createProduct(Product product) {
        repo.ifNewProductOrElse(product, this::createNewProduct, this::rejectProduct);
    }

    private void createNewProduct(Product product) {
        //TODO add new product to database!
    }

    private void rejectProduct() {
        //TODO throw exception and catch it in the Infra layer and log it all
    }

}

package com.ecommerce.product_service.domain.repository;

import java.util.Optional;

import com.ecommerce.product_service.domain.model.Product;

public interface ProductRepository {

    void save(Product product);

    Optional<Product> findById(int productId);

}

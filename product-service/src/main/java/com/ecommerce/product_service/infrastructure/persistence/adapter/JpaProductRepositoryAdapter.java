package com.ecommerce.product_service.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ecommerce.product_service.application.port.ProductRepository;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.infrastructure.persistence.entity.ProductJpaEntity;
import com.ecommerce.product_service.infrastructure.persistence.repository.SpringDataProductRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JpaProductRepositoryAdapter implements ProductRepository {

    private final SpringDataProductRepository jpaRepo;

    @Override
    public void save(Product product) {
        log.info("JpaProductRepositoryAdapter::save begins.");

        ProductJpaEntity entity = ProductJpaEntity.fromDomain(product);
        jpaRepo.save(entity);
        log.debug("Data is saving to product table...");

        log.info("JpaProductRepositoryAdapter::save ends.");
    }

    @Override
    public Optional<Product> findById(int productId) {
        return jpaRepo.findById(productId).map(ProductJpaEntity::toDomain);
    }   
}

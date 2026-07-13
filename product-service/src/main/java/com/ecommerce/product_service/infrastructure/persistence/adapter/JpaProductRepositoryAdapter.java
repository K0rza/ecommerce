package com.ecommerce.product_service.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.repository.ProductRepository;
import com.ecommerce.product_service.infrastructure.persistence.entity.ProductJpaEntity;
import com.ecommerce.product_service.infrastructure.persistence.repository.SpringDataProductRepository;

@Repository
public class JpaProductRepositoryAdapter implements ProductRepository {

    private SpringDataProductRepository jpaRepo;

    public JpaProductRepositoryAdapter(SpringDataProductRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public void save(Product product) {
        ProductJpaEntity entity = ProductJpaEntity.fromDomain(product);

        jpaRepo.save(entity);
    }

    @Override
    public Optional<Product> findById(int productId) {
        return jpaRepo.findById(productId).map(ProductJpaEntity::toDomain);
    }
    

}

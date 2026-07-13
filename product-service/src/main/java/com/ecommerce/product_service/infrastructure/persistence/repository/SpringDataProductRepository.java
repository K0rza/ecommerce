package com.ecommerce.product_service.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.product_service.infrastructure.persistence.entity.ProductJpaEntity;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, Integer> {

}

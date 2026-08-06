package com.ecommerce.inventory_service.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.inventory_service.infrastructure.repository.dto.ProductEntity;

public interface JpaProductInterface extends JpaRepository<ProductEntity, Integer>{

}

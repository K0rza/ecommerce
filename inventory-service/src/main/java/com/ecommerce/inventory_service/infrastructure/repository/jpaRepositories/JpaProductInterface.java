package com.ecommerce.inventory_service.infrastructure.repository.jpaRepositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.inventory_service.infrastructure.repository.dto.ProductDto;

public interface JpaProductInterface extends JpaRepository<ProductDto, Integer>{

    Optional<ProductDto> findByEventId(int eventId);
}

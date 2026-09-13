package com.ecommerce.inventory_service.infrastructure.repository.jpaRepositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.inventory_service.infrastructure.repository.dto.OrderDto;

public interface JpaOrderInterface extends JpaRepository<OrderDto, Integer>{

}

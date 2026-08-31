package com.ecommerce.inventory_service.infrastructure.repository.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "inventory")
public class InventoryDto {

    @Id
    private int productId;
    private int stock;

    public InventoryDto(int produtcId, int stock) {
        this.productId = produtcId;
        this.stock = stock;
    }
}

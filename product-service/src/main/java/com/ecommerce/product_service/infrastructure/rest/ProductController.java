package com.ecommerce.product_service.infrastructure.rest;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.product_service.application.command.CreateProductCommand;
import com.ecommerce.product_service.application.usecase.CreateProductUseCase;
import com.ecommerce.product_service.infrastructure.exception.InventoryCircuitBreaker;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private  final CreateProductUseCase useCase;
    private final InventoryCircuitBreaker inventoryCircuitBreaker;

    public record CreateProductRequest(
        String title,
        String sku,
        BigDecimal priceAmount,
        String currency,
        int stock
    ) {}

    @PostMapping()
    public ResponseEntity<Integer> postMethodName(@RequestBody CreateProductRequest request) {
     
        CreateProductCommand command = new CreateProductCommand(
            request.title(), 
            request.sku(),
            request.priceAmount(), 
            request.currency(), 
            request.stock());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(useCase.execute(command));
    }

    @GetMapping("/{product}")
    public ResponseEntity<Integer> getProductName(@PathVariable("product") String product) {
        int stock = inventoryCircuitBreaker.checkStock(product);
        return ResponseEntity.ok(stock);
    }
   
}

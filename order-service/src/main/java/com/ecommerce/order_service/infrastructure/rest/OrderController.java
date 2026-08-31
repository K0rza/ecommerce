package com.ecommerce.order_service.infrastructure.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order_service.application.usecases.CreateOrderUseCase;
import com.ecommerce.order_service.domain.entity.OrderRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase useCase;

    @GetMapping("/api/orders")
    public void getOrders(@RequestBody OrderRequest orderRequest) {
        useCase.process(orderRequest);
    }

}

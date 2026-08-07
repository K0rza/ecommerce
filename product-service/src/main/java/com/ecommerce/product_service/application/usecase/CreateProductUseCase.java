package com.ecommerce.product_service.application.usecase;

import java.util.Random;

import com.ecommerce.product_service.application.command.CreateProductCommand;
import com.ecommerce.product_service.application.port.ProductEventPublisher;
import com.ecommerce.product_service.domain.event.ProductCreatedEvent;
import com.ecommerce.product_service.domain.exception.InvalidPriceException;
import com.ecommerce.product_service.domain.exception.InvalidSkuException;
import com.ecommerce.product_service.domain.model.Price;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.model.Sku;
import com.ecommerce.product_service.domain.repository.ProductRepository;

public class CreateProductUseCase {

    private final ProductRepository productRepository;
    private final ProductEventPublisher publisher;

    public CreateProductUseCase(ProductRepository productRepository, ProductEventPublisher publisher) {
        this.productRepository = productRepository;
        this.publisher = publisher;
    }

    public int execute(CreateProductCommand command) throws InvalidPriceException, InvalidSkuException {
        int productId = new Random().nextInt(100);
        Sku sku = new Sku(command.sku());
        Price price = new Price(command.price(), command.currency());

        //Aggragate Root
        Product product = new Product(productId, price, sku, command.title(), command.stock(), command.version());
        
        //Port
        productRepository.save(product);
        publisher.publish(new ProductCreatedEvent(productId, command.stock()));

        return product.getProductId();
    }
}

package com.ecommerce.product_service.application.usecase;

import com.ecommerce.product_service.application.command.CreateProductCommand;
import com.ecommerce.product_service.domain.model.Price;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.model.Sku;
import com.ecommerce.product_service.domain.repository.ProductRepository;

public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int execute(CreateProductCommand command) {
        int productId = 10;
        Sku sku = new Sku(command.sku());
        Price price = new Price(command.price(), command.currency());

        //Aggragate Root
        Product product = new Product(productId, price, sku, command.title(), command.stock());
        
        //Port
        productRepository.save(product);

        return product.getProductId();
    }

}

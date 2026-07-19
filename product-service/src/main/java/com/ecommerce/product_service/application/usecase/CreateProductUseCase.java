package com.ecommerce.product_service.application.usecase;

import java.util.Random;

import com.ecommerce.product_service.application.command.CreateProductCommand;
import com.ecommerce.product_service.domain.exception.InvalidPriceException;
import com.ecommerce.product_service.domain.exception.InvalidSkuException;
import com.ecommerce.product_service.domain.model.Price;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.model.Sku;
import com.ecommerce.product_service.domain.repository.ProductRepository;

public class CreateProductUseCase {

    private final ProductRepository productRepository;

    public CreateProductUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public int execute(CreateProductCommand command) throws InvalidPriceException, InvalidSkuException {
        int productId = new Random().nextInt(100);
        Sku sku = new Sku(command.sku());
        Price price = new Price(command.price(), command.currency());

        //Aggragate Root
        Product product = new Product(productId, price, sku, command.title(), command.stock());
        
        //Port
        productRepository.save(product);

        return product.getProductId();
    }

}

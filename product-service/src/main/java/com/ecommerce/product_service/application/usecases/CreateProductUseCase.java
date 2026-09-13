package com.ecommerce.product_service.application.usecases;

import java.util.Random;

import com.ecommerce.product_service.application.command.CreateProductCommand;
import com.ecommerce.product_service.application.port.ProductCreationPort;
import com.ecommerce.product_service.domain.exception.InvalidPriceException;
import com.ecommerce.product_service.domain.exception.InvalidSkuException;
import com.ecommerce.product_service.domain.model.Price;
import com.ecommerce.product_service.domain.model.Product;
import com.ecommerce.product_service.domain.model.Sku;

public class CreateProductUseCase {

    private final ProductCreationPort productCreationPort;

    public CreateProductUseCase(ProductCreationPort productCreationPort) {
        this.productCreationPort = productCreationPort;
    }

    public int execute(CreateProductCommand command) throws InvalidPriceException, InvalidSkuException {
        int productId = new Random().nextInt(100);
        Sku sku = new Sku(command.sku());
        Price price = new Price(command.price(), command.currency());

        //Aggragate Root
        Product product = new Product(productId, price, sku, command.title(), command.stock(), command.version());
        
        //Port
        productCreationPort.saveAndPublishEvent(product);

        return product.getProductId();
    }
}

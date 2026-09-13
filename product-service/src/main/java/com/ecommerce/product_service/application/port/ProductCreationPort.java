package com.ecommerce.product_service.application.port;

import com.ecommerce.product_service.domain.model.Product;

public interface ProductCreationPort {

    void saveAndPublishEvent(Product product);

}

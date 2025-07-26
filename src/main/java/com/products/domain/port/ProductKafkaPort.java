package com.products.domain.port;

import com.products.domain.model.Product;

public interface ProductKafkaPort {

    void publishEvent(Product product);

}
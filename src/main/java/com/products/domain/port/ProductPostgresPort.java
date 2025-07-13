package com.products.domain.port;

import com.products.domain.model.Product;

public interface ProductPostgresPort {

    Product save(Product product);

    void deactivateProduct(Long id);

}
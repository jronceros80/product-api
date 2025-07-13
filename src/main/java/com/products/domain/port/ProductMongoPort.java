package com.products.domain.port;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;

import java.util.Optional;

public interface ProductMongoPort {

    void save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findActiveById(Long id);

    PaginatedResult<Product> findActiveProducts(PaginationQuery paginationQuery, ProductFilter filter);
}
package com.products.domain.port;

import java.util.Optional;

import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;

public interface ProductMongoPort {

    void save(Product product);

    Optional<Product> findById(Long id);

    Optional<Product> findActiveById(Long id);

    PaginatedResult<Product> findActiveProducts(PaginationQuery paginationQuery, ProductFilter filter);

}
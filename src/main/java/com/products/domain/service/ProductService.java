package com.products.domain.service;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductPostgresPort;
import com.products.domain.port.ProductKafkaPort;
import com.products.domain.port.ProductMongoPort;

public class ProductService {

    private final ProductPostgresPort productPostgresPort;
    private final ProductMongoPort productMongoPort;
    private final ProductKafkaPort productEventPort;

    public ProductService(
            final ProductPostgresPort productPostgresPort,
            final ProductMongoPort productPersistenceMongoPort,
            final ProductKafkaPort productEventPort) {

        this.productPostgresPort = productPostgresPort;
        this.productMongoPort = productPersistenceMongoPort;
        this.productEventPort = productEventPort;
    }

    public Product createProduct(final Product product) {
        final Product savedProduct = productPostgresPort.save(product);
        productEventPort.publishEvent(savedProduct);
        return savedProduct;
    }

    public PaginatedResult<Product> getAllActiveProducts(final PaginationQuery paginationQuery,
            final ProductFilter filter) {

        return productMongoPort.findActiveProducts(paginationQuery, filter);
    }

    public Product getActiveProductById(final Long id) {
        return productMongoPort.findActiveById(id)
                .orElseThrow(() -> new ProductNotFoundException("Active product not found with id: " + id));
    }

    public Product getById(final Long id) {
        return productMongoPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

    public Product updateProduct(final Long id, final Product productUpdate) {
        final Product existingProduct = productMongoPort.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        final Product updatedProduct = new Product(
                existingProduct.id(),
                productUpdate.name(),
                productUpdate.price(),
                productUpdate.category(),
                productUpdate.active());

        final Product savedProduct = productPostgresPort.save(updatedProduct);
        productEventPort.publishEvent(savedProduct);

        return savedProduct;
    }

    public void deactivateProduct(final Product product) {
        productPostgresPort.deactivateProduct(product.id());
        productEventPort.publishEvent(product);
    }
}
package com.products.infrastructure.adapter;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.Product;
import com.products.domain.port.ProductPostgresPort;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.postgresql.repository.ProductJpaRepository;
import com.products.infrastructure.mapper.ProductMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ProductPostgresAdapter implements ProductPostgresPort {

    private static final Logger logger = LoggerFactory.getLogger(ProductPostgresAdapter.class);

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;

    public ProductPostgresAdapter(ProductJpaRepository productJpaRepository, ProductMapper productMapper) {
        this.productJpaRepository = productJpaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product save(final Product product) {
        final ProductEntity entity = productMapper.domainToEntity(product);
        final ProductEntity savedEntity = productJpaRepository.save(entity);
        return productMapper.entityToDomain(savedEntity);
    }

    @Override
    public void deactivateProduct(Long id) {
        final ProductEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (!entity.isActive()) {
            logger.info("Product {} is already inactive, returning success", id);
            return;
        }

        entity.setActive(false);
        productJpaRepository.save(entity);
    }
}
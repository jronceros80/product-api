package com.products.infrastructure.adapter;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductPersistencePort;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.postgresql.repository.ProductJpaRepository;
import com.products.infrastructure.mapper.ProductMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Transactional
public class ProductAdapter implements ProductPersistencePort {

    private final ProductJpaRepository productJpaRepository;
    private final ProductMapper productMapper;
    private static final Logger logger = LoggerFactory.getLogger(ProductAdapter.class);

    private final Set<Long> deactivatedInSession = ConcurrentHashMap.newKeySet();

    public ProductAdapter(ProductJpaRepository productJpaRepository, ProductMapper productMapper) {
        this.productJpaRepository = productJpaRepository;
        this.productMapper = productMapper;
    }

    @Override
    public Product save(final Product product) {
        final ProductEntity entity = productMapper.toEntity(product);
        final ProductEntity savedEntity = productJpaRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(final Long id) {
        return productJpaRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findActiveById(final Long id) {
        return productJpaRepository.findByIdAndActiveTrue(id)
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResult<Product> findActiveProducts(final PaginationQuery paginationQuery,
            final ProductFilter filter) {

        final Pageable pageable = productMapper.toPageable(paginationQuery);

        ProductCategory category = null;
        if (filter.getCategoryForQuery() != null) {
            category = ProductCategory.valueOf(filter.getCategoryForQuery().toUpperCase());
        }

        final Page<ProductEntity> entityPage = productJpaRepository.findProductsWithFilters(
                filter.active(),
                category,
                filter.getNameForQuery(),
                pageable);

        final Page<Product> productPage = entityPage.map(productMapper::toDomain);

        return new PaginatedResult<>(
                productPage.getContent(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getNumber(),
                productPage.getSize());
    }

    @Override
    public void deactivateProduct(Long id) {
        final ProductEntity entity = productJpaRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        if (!entity.isActive()) {
            if (deactivatedInSession.contains(id)) {
                throw new ProductNotFoundException("Product with id " + id + " is already inactive");
            }
            logger.info("Product {} is already inactive, returning success", id);
            return;
        }

        entity.setActive(false);
        productJpaRepository.save(entity);
        deactivatedInSession.add(id);
    }
}
package com.products.infrastructure.adapter;

import com.products.domain.exception.ProductNotFoundException;
import com.products.domain.model.PaginatedResult;
import com.products.domain.model.PaginationQuery;
import com.products.domain.model.Product;
import com.products.domain.model.ProductFilter;
import com.products.domain.port.ProductPersistencePort;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.postgresql.repository.ProductJpaRepository;
import com.products.infrastructure.mapper.ProductMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

        final Long cursor = parseCursor(paginationQuery.cursor());
        final int limit = paginationQuery.limit();
        final String categoryStr = filter.getCategoryForQuery();
        final String name = filter.getNameForQuery();
        final Boolean active = filter.active();

        final List<ProductEntity> entities = productJpaRepository.findProductsAfterCursor(
                cursor, active, categoryStr, name, limit + 1);

        final boolean hasNext = entities.size() > limit;
        final List<ProductEntity> actualEntities = hasNext ? entities.subList(0, limit) : entities;

        final List<Product> products = actualEntities.stream()
                .map(productMapper::toDomain)
                .toList();

        String nextCursor = null;
        String previousCursor = null;
        boolean hasPrevious = cursor != null;

        if (!products.isEmpty()) {
            nextCursor = String.valueOf(products.getLast().id());
        }

        if (hasPrevious && !products.isEmpty()) {
            previousCursor = String.valueOf(products.getFirst().id());
        }

        return new PaginatedResult<>(
                products,
                nextCursor,
                previousCursor,
                hasNext,
                hasPrevious,
                products.size(),
                limit);
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

    private Long parseCursor(String cursor) {
        if (cursor == null || cursor.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(cursor.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid cursor format: {}", cursor);
            return null;
        }
    }
}
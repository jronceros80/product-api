package com.products.infrastructure.postgresql.repository;

import com.products.infrastructure.postgresql.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

        Optional<ProductEntity> findByIdAndActiveTrue(Long id);

        @Query("SELECT p FROM ProductEntity p WHERE " +
                        "(:active IS NULL OR p.active = :active) AND " +
                        "(:category IS NULL OR p.category = :category) AND " +
                        "(:name IS NULL OR LOWER(CAST(p.name AS string)) LIKE LOWER(CONCAT('%', CAST(:name AS string), '%')))")
        Page<ProductEntity> findProductsWithFilters(
                        @Param("active") Boolean active,
                        @Param("category") com.products.domain.model.ProductCategory category,
                        @Param("name") String name,
                        Pageable pageable);
}
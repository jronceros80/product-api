package com.products.infrastructure.postgresql.repository;

import com.products.infrastructure.postgresql.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long> {

        Optional<ProductEntity> findByIdAndActiveTrue(Long id);

        @Query(value = "SELECT * FROM products WHERE " +
                        "(:cursor IS NULL OR id > :cursor) AND " +
                        "(:active IS NULL OR active = :active) AND " +
                        "(:category IS NULL OR category = CAST(:category AS varchar)) AND " +
                        "(:name IS NULL OR LOWER(name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
                        "ORDER BY id ASC LIMIT :limit", nativeQuery = true)
        List<ProductEntity> findProductsAfterCursor(
                        @Param("cursor") Long cursor,
                        @Param("active") Boolean active,
                        @Param("category") String category,
                        @Param("name") String name,
                        @Param("limit") int limit);
}
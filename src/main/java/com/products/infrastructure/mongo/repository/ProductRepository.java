package com.products.infrastructure.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.products.infrastructure.mongo.document.ProductDocument;

import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<ProductDocument, Long>, ProductRepositoryCustom {

    Optional<ProductDocument> findByIdAndActiveTrue(Long id);

}

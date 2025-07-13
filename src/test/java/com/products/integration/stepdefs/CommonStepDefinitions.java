package com.products.integration.stepdefs;

import com.products.domain.model.Product;
import com.products.domain.model.ProductCategory;
import com.products.domain.port.ProductKafkaPort;
import com.products.infrastructure.mapper.ProductMapper;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.postgresql.repository.ProductJpaRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CommonStepDefinitions {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private ProductKafkaPort productEventPort;

    @Autowired
    private ProductMapper productMapper;

    private Long firstProductId;

    @Given("the application is running with a clean database")
    public void theApplicationIsRunningWithACleanDatabase() {
        productRepository.deleteAll();
    }

    @Given("the following products exist:")
    @Transactional
    public void theFollowingProductsExist(DataTable dataTable) {
        List<Map<String, String>> products = dataTable.asMaps();
        for (Map<String, String> productData : products) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setName(productData.get("name"));
            productEntity.setPrice(new BigDecimal(productData.get("price")));
            productEntity.setCategory(ProductCategory.valueOf(productData.get("category").toUpperCase()));
            productEntity.setActive(Boolean.parseBoolean(productData.get("active")));

            ProductEntity productEntitySaved =  productRepository.save(productEntity);
            Product product = productMapper.entityToDomain(productEntitySaved);
            productEventPort.publishEvent(product);
        }
        productRepository.flush();
    }

    @Given("a product exists with ID {long}")
    @Transactional
    public void aProductExistsWithId(Long id) {
        List<ProductEntity> existingProducts = productRepository.findAll();
        if (!existingProducts.isEmpty()) {
            firstProductId = existingProducts.getFirst().getId();
        }
    }

    public Long getFirstProductId() {
        return firstProductId;
    }
}
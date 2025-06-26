package com.products.integration.stepdefs;

import com.products.domain.model.ProductCategory;
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
            ProductEntity product = new ProductEntity();
            product.setName(productData.get("name"));
            product.setPrice(new BigDecimal(productData.get("price")));
            product.setCategory(ProductCategory.valueOf(productData.get("category").toUpperCase()));
            product.setActive(Boolean.parseBoolean(productData.get("active")));

            productRepository.save(product);
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
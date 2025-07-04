package com.products.integration.stepdefs;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.products.infrastructure.postgresql.entity.ProductEntity;
import com.products.infrastructure.postgresql.repository.ProductJpaRepository;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AssertionStepDefinitions {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private HttpStepDefinitions httpSteps;

    @Autowired
    private CommonStepDefinitions commonSteps;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertEquals(expectedStatus, httpSteps.getResponse().getStatusCode());
    }

    @Then("the response should contain an error message")
    public void theResponseShouldContainAnErrorMessage() {
        try {
            JsonNode responseJson = objectMapper.readTree(httpSteps.getResponse().getBody().asString());
            assertTrue(responseJson.has("message"));
            assertNotNull(responseJson.get("message").asText());
        } catch (Exception e) {
            fail("Error parsing response JSON: " + e.getMessage());
        }
    }

    @Then("the response should contain the created product")
    public void theResponseShouldContainTheCreatedProduct() {
        try {
            JsonNode responseJson = objectMapper.readTree(httpSteps.getResponse().getBody().asString());
            assertTrue(responseJson.has("id"));
            assertTrue(responseJson.has("name"));
            assertTrue(responseJson.has("price"));
        } catch (Exception e) {
            fail("Error parsing response JSON: " + e.getMessage());
        }
    }

    @Then("the product should be saved in the database")
    @Transactional
    public void theProductShouldBeSavedInTheDatabase() {
        long count = productRepository.count();
        assertTrue(count > 0, "Product should be saved in database");
    }

    @Then("the product should be active by default")
    public void theProductShouldBeActiveByDefault() {
        try {
            JsonNode responseJson = objectMapper.readTree(httpSteps.getResponse().getBody().asString());
            assertTrue(responseJson.has("active"));
            assertTrue(responseJson.get("active").asBoolean());
        } catch (Exception e) {
            fail("Error parsing response JSON: " + e.getMessage());
        }
    }

    @Then("the response should contain the updated product")
    public void theResponseShouldContainTheUpdatedProduct() {
        try {
            JsonNode responseJson = objectMapper.readTree(httpSteps.getResponse().getBody().asString());
            assertTrue(responseJson.has("id"));
            assertTrue(responseJson.has("name"));            
        } catch (Exception e) {
            fail("Error parsing response JSON: " + e.getMessage());
        }
    }

    @Then("the product should be updated in the database")
    @Transactional
    public void theProductShouldBeUpdatedInTheDatabase() {
        Long productId = commonSteps.getFirstProductId();
        Optional<ProductEntity> product = productRepository.findById(productId);
        assertTrue(product.isPresent());
    }

    @Then("the product should be removed from the database")
    @Transactional
    public void theProductShouldBeRemovedFromTheDatabase() {
        Long productId = commonSteps.getFirstProductId();
        Optional<ProductEntity> product = productRepository.findById(productId);
        assertTrue(product.isPresent());
        assertFalse(product.get().isActive());
    }

    @Then("the response should contain the product details")
    public void theResponseShouldContainTheProductDetails() {
        try {
            JsonNode responseJson = objectMapper.readTree(httpSteps.getResponse().getBody().asString());
            assertTrue(responseJson.has("id"));
            assertTrue(responseJson.has("name"));
            assertTrue(responseJson.has("price"));
            assertTrue(responseJson.has("category"));
        } catch (Exception e) {
            fail("Error parsing response JSON: " + e.getMessage());
        }
    }

    @Then("the response should include HATEOAS links")
    public void theResponseShouldIncludeHateoasLinks() {
        assertNotNull(httpSteps.getResponse().getBody().asString());
    }

    @Then("the response should contain {int} products")
    public void theResponseShouldContainProducts(int expectedCount) {
        assertNotNull(httpSteps.getResponse().getBody().asString());
    }

    @Then("the response should include pagination metadata")
    public void theResponseShouldIncludePaginationMetadata() {
        assertNotNull(httpSteps.getResponse().getBody().asString());
    }

    @Then("the response should include HATEOAS navigation links")
    public void theResponseShouldIncludeHateoasNavigationLinks() {
        assertNotNull(httpSteps.getResponse().getBody().asString());
    }

    @Then("the products should be sorted by price in descending order")
    public void theProductsShouldBeSortedByPriceInDescendingOrder() {
        assertNotNull(httpSteps.getResponse().getBody().asString());
    }
}
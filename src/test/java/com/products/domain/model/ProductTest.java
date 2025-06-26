package com.products.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateProductWithAllFields() {
        Long id = 1L;
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        ProductCategory category = ProductCategory.ELECTRONICS;
        boolean active = true;

        Product product = new Product(id, name, price, category, active);

        assertAll("Product creation with all fields",
                () -> assertEquals(id, product.id()),
                () -> assertEquals(name, product.name()),
                () -> assertEquals(price, product.price()),
                () -> assertEquals(category, product.category()),
                () -> assertEquals(active, product.active())
        );
    }

    @Test
    void shouldCreateProductWithNullId() {
        String name = "New Product";
        BigDecimal price = new BigDecimal("49.99");
        ProductCategory category = ProductCategory.BOOKS;
        boolean active = false;

        Product product = new Product(null, name, price, category, active);

        assertAll("Product creation with null id",
                () -> assertNull(product.id()),
                () -> assertEquals(name, product.name()),
                () -> assertEquals(price, product.price()),
                () -> assertEquals(category, product.category()),
                () -> assertEquals(active, product.active())
        );
    }

    @Test
    void shouldCreateProductWithDefaultActiveValue() {
        String name = "Default Active Product";
        BigDecimal price = new BigDecimal("29.99");
        ProductCategory category = ProductCategory.CLOTHING;

        Product product = new Product(name, price, category);

        assertAll("Product creation with default active value",
                () -> assertNull(product.id()),
                () -> assertEquals(name, product.name()),
                () -> assertEquals(price, product.price()),
                () -> assertEquals(category, product.category()),
                () -> assertTrue(product.active())
        );
    }

    @Test
    void shouldCreateProductWithIdAndDefaultActiveValue() {
        Long id = 2L;
        String name = "Product with ID";
        BigDecimal price = new BigDecimal("39.99");
        ProductCategory category = ProductCategory.ELECTRONICS;

        Product product = new Product(id, name, price, category);

        assertAll("Product creation with id and default active value",
                () -> assertEquals(id, product.id()),
                () -> assertEquals(name, product.name()),
                () -> assertEquals(price, product.price()),
                () -> assertEquals(category, product.category()),
                () -> assertTrue(product.active())
        );
    }

    @Test
    void shouldHandleDifferentProductCategories() {
        Product electronicsProduct = new Product(
                1L, "Laptop", new BigDecimal("999.99"), ProductCategory.ELECTRONICS, true);

        Product clothingProduct = new Product(2L, "T-Shirt", new BigDecimal("29.99"), ProductCategory.CLOTHING, true);
        Product booksProduct = new Product(3L, "Java Book", new BigDecimal("59.99"), ProductCategory.BOOKS, true);

        assertAll("Different product categories",
                () -> assertEquals(ProductCategory.ELECTRONICS, electronicsProduct.category()),
                () -> assertEquals(ProductCategory.CLOTHING, clothingProduct.category()),
                () -> assertEquals(ProductCategory.BOOKS, booksProduct.category())
        );
    }

    @Test
    void shouldHandleDifferentPriceValues() {
        Product cheapProduct = new Product(1L, "Cheap Item", new BigDecimal("0.01"), ProductCategory.BOOKS, true);

        Product expensiveProduct = new Product(
                2L, "Expensive Item", new BigDecimal("9999.99"), ProductCategory.ELECTRONICS, true);

        Product freeProduct = new Product(3L, "Free Item", BigDecimal.ZERO, ProductCategory.BOOKS, true);

        assertAll("Different price values",
                () -> assertEquals(new BigDecimal("0.01"), cheapProduct.price()),
                () -> assertEquals(new BigDecimal("9999.99"), expensiveProduct.price()),
                () -> assertEquals(BigDecimal.ZERO, freeProduct.price())
        );
    }

    @Test
    void shouldHandleActiveAndInactiveProducts() {
        Product activeProduct = new Product(
                1L, "Active Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);

        Product inactiveProduct = new Product(
                2L, "Inactive Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, false);

        assertAll("Active and inactive products",
                () -> assertTrue(activeProduct.active()),
                () -> assertFalse(inactiveProduct.active())
        );
    }

    @Test
    void shouldTestRecordEquality() {
        Product product1 = new Product(1L, "Test Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);
        Product product2 = new Product(1L, "Test Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);
        Product product3 = new Product(
                2L, "Different Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);

        assertAll("Record equality and hashCode",
                () -> assertEquals(product1, product2),
                () -> assertNotEquals(product1, product3),
                () -> assertEquals(product1.hashCode(), product2.hashCode()),
                () -> assertNotEquals(product1.hashCode(), product3.hashCode())
        );
    }

    @Test
    void shouldTestToStringMethod() {
        Product product = new Product(1L, "Test Product", new BigDecimal("99.99"), ProductCategory.ELECTRONICS, true);

        String toString = product.toString();

        assertAll("ToString method validation",
                () -> assertNotNull(toString),
                () -> assertTrue(toString.contains("Test Product")),
                () -> assertTrue(toString.contains("99.99")),
                () -> assertTrue(toString.contains("ELECTRONICS"))
        );
    }
} 
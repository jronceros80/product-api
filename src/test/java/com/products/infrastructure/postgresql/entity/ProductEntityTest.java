package com.products.infrastructure.postgresql.entity;

import com.products.domain.model.ProductCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ProductEntityTest {

    @Test
    void createProductEntity_WithDefaultConstructor_ShouldInitializeCorrectly() {
        ProductEntity entity = new ProductEntity();

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isNull();
        assertThat(entity.getPrice()).isNull();
        assertThat(entity.getCategory()).isNull();
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void createProductEntity_WithFullConstructor_ShouldSetAllFields() {
        String name = "Test Product";
        BigDecimal price = new BigDecimal("99.99");
        ProductCategory category = ProductCategory.ELECTRONICS;
        boolean active = true;

        ProductEntity entity = new ProductEntity(name, price, category, active);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getCategory()).isEqualTo(category);
        assertThat(entity.isActive()).isEqualTo(active);
    }

    @Test
    void createProductEntity_WithoutIdAndNullActive_ShouldDefaultActiveToTrue() {
        String name = "Test Product";
        BigDecimal price = new BigDecimal("29.99");
        ProductCategory category = ProductCategory.CLOTHING;

        ProductEntity entity = new ProductEntity(name, price, category, null);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getCategory()).isEqualTo(category);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void createProductEntity_WithoutIdAndActive_ShouldDefaultActiveToTrue() {
        String name = "Test Product";
        BigDecimal price = new BigDecimal("19.99");
        ProductCategory category = ProductCategory.BOOKS;

        ProductEntity entity = new ProductEntity(name, price, category, true);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getCategory()).isEqualTo(category);
        assertThat(entity.isActive()).isTrue();
    }

    @Test
    void setAndGetId_ShouldWorkCorrectly() {
        ProductEntity entity = new ProductEntity();
        Long id = 100L;

        entity.setId(id);

        assertThat(entity.getId()).isEqualTo(id);
    }

    @Test
    void setAndGetName_ShouldWorkCorrectly() {
        ProductEntity entity = new ProductEntity();
        String name = "Updated Product Name";

        entity.setName(name);

        assertThat(entity.getName()).isEqualTo(name);
    }

    @Test
    void setAndGetPrice_ShouldWorkCorrectly() {
        ProductEntity entity = new ProductEntity();
        BigDecimal price = new BigDecimal("199.99");

        entity.setPrice(price);

        assertThat(entity.getPrice()).isEqualTo(price);
    }

    @Test
    void setAndGetCategory_ShouldWorkCorrectly() {
        ProductEntity entity = new ProductEntity();
        ProductCategory category = ProductCategory.ELECTRONICS;

        entity.setCategory(category);

        assertThat(entity.getCategory()).isEqualTo(category);
    }

    @Test
    void setAndGetActive_ShouldWorkCorrectly() {
        ProductEntity entity = new ProductEntity();
        boolean active = false;

        entity.setActive(active);

        assertThat(entity.isActive()).isEqualTo(active);
    }

    @Test
    void equals_ShouldCompareAllFields() {
        ProductEntity entity1 = new ProductEntity("Product 1", new BigDecimal("10.00"), ProductCategory.BOOKS, true);
        entity1.setId(1L);
        
        ProductEntity entity2 = new ProductEntity("Product 1", new BigDecimal("10.00"), ProductCategory.BOOKS, true);
        entity2.setId(1L);
        
        ProductEntity entity3 = new ProductEntity("Product 2", new BigDecimal("20.00"),
                ProductCategory.ELECTRONICS, false);
        entity3.setId(2L);

        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1).isNotEqualTo(entity3);
        assertThat(entity1).isNotEqualTo(null);
    }

    @Test
    void hashCode_ShouldBeConsistentWithEquals() {
        ProductEntity entity1 = new ProductEntity("Product 1", new BigDecimal("10.00"), ProductCategory.BOOKS, true);
        entity1.setId(1L);
        
        ProductEntity entity2 = new ProductEntity("Product 1", new BigDecimal("10.00"), ProductCategory.BOOKS, true);
        entity2.setId(1L);

        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        ProductEntity entity = new ProductEntity("Test Product", new BigDecimal("99.99"),
                ProductCategory.ELECTRONICS, true);
        entity.setId(1L);

        String stringRepresentation = entity.toString();

        assertThat(stringRepresentation)
            .contains("id=1")
            .contains("name='Test Product'")
            .contains("price=99.99")
            .contains("category=ELECTRONICS")
            .contains("active=true");
    }

    @Test
    void createProductEntity_WithAllCategoryValues_ShouldWorkCorrectly() {
        String name = "Test Product";
        BigDecimal price = new BigDecimal("10.00");

        for (ProductCategory category : ProductCategory.values()) {
            ProductEntity entity = new ProductEntity(name, price, category, true);
            assertThat(entity.getCategory()).isEqualTo(category);
        }
    }

    @Test
    void createProductEntity_WithPrecisePrice_ShouldMaintainPrecision() {
        String name = "Precise Product";
        BigDecimal price = new BigDecimal("123.456789");
        ProductCategory category = ProductCategory.ELECTRONICS;

        ProductEntity entity = new ProductEntity(name, price, category, true);

        assertThat(entity.getPrice()).isEqualTo(price);
        assertThat(entity.getPrice().toString()).isEqualTo("123.456789");
    }
}
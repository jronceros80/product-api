package com.products.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductCategoryTest {

    @Test
    void shouldHaveAllExpectedEnumValues() {
        ProductCategory[] categories = ProductCategory.values();

        assertAll("Expected enum values",
                () -> assertEquals(3, categories.length),
                () -> assertEquals(ProductCategory.ELECTRONICS, categories[0]),
                () -> assertEquals(ProductCategory.CLOTHING, categories[1]),
                () -> assertEquals(ProductCategory.BOOKS, categories[2])
        );
    }

    @Test
    void shouldConvertStringToEnumValue() {
        assertAll("String to enum conversion",
                () -> assertEquals(ProductCategory.ELECTRONICS, ProductCategory.valueOf("ELECTRONICS")),
                () -> assertEquals(ProductCategory.CLOTHING, ProductCategory.valueOf("CLOTHING")),
                () -> assertEquals(ProductCategory.BOOKS, ProductCategory.valueOf("BOOKS"))
        );
    }

    @Test
    void shouldThrowExceptionForInvalidEnumValue() {
        assertThrows(IllegalArgumentException.class, () -> {
            ProductCategory.valueOf("INVALID_CATEGORY");
        });
    }

    @Test
    void shouldMaintainEnumEquality() {
        ProductCategory electronics1 = ProductCategory.ELECTRONICS;
        ProductCategory electronics2 = ProductCategory.valueOf("ELECTRONICS");

        assertAll("Enum equality",
                () -> assertEquals(electronics1, electronics2),
                () -> assertSame(electronics1, electronics2)
        );
    }

    @Test
    void shouldHaveCorrectStringRepresentation() {
        assertAll("String representation",
                () -> assertEquals("ELECTRONICS", ProductCategory.ELECTRONICS.toString()),
                () -> assertEquals("CLOTHING", ProductCategory.CLOTHING.toString()),
                () -> assertEquals("BOOKS", ProductCategory.BOOKS.toString())
        );
    }

    @Test
    void shouldHaveCorrectOrdinalValues() {
        assertAll("Ordinal values",
                () -> assertEquals(0, ProductCategory.ELECTRONICS.ordinal()),
                () -> assertEquals(1, ProductCategory.CLOTHING.ordinal()),
                () -> assertEquals(2, ProductCategory.BOOKS.ordinal())
        );
    }

    @Test
    void shouldSupportSwitchStatements() {
        ProductCategory category = ProductCategory.ELECTRONICS;
        String result = switch (category) {
            case ELECTRONICS -> "Electronic device";
            case CLOTHING -> "Clothing item";
            case BOOKS -> "Book item";
            default -> "Unknown category";
        };

        assertEquals("Electronic device", result);
    }

    @Test
    void shouldBeComparable() {
        ProductCategory electronics = ProductCategory.ELECTRONICS;
        ProductCategory clothing = ProductCategory.CLOTHING;
        ProductCategory books = ProductCategory.BOOKS;

        assertAll("Enum comparability",
                () -> assertTrue(electronics.compareTo(clothing) < 0),
                () -> assertTrue(clothing.compareTo(books) < 0),
                () -> assertTrue(books.compareTo(electronics) > 0),
                () -> assertEquals(0, electronics.compareTo(ProductCategory.ELECTRONICS))
        );
    }
} 
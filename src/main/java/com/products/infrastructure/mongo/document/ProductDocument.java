package com.products.infrastructure.mongo.document;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class ProductDocument {

    @Id
    private final Long id;
    private final String name;
    private final BigDecimal price;
    private final String category;
    private final Boolean active;

    public ProductDocument(Long id, String name, BigDecimal price, String category, boolean active) {
        this.id = Objects.requireNonNull(id, "product ID cannot be null");
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.price = Objects.requireNonNull(price, "price cannot be null");
        this.category = Objects.requireNonNull(category, "category cannot be null");
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Boolean getActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductDocument that = (ProductDocument) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringBuilder("ProductDocument{")
                .append("id='").append(id).append('\'')
                .append(", name='").append(name).append('\'')
                .append(", price=").append(price)
                .append(", category=").append(category)
                .append(", active=").append(active)
                .append('}')
                .toString();
    }

}

package com.products.infrastructure.mongo.repository;

import com.products.infrastructure.mongo.document.ProductDocument;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private static final String ACTIVE = "active";

    private static final String CATEGORY = "category";

    private static final String NAME = "name";

    private static final String ID = "id";

    @NotNull
    private final MongoTemplate mongoTemplate;

    public ProductRepositoryCustomImpl(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ProductDocument> findProductsAfterCursor(
            final Long cursor, final Boolean active, final String category, final String name, final int limit) {

        Query query = new Query();
        query.with(Sort.by(Sort.Direction.ASC, ID));

        Criteria criteria = new Criteria();
        if (cursor != null) {
            criteria.and(ID).gt(cursor);
        }

        if (active != null) {
            criteria.and(ACTIVE).is(active);
        }

        if (StringUtils.isNotBlank(category)) {
            criteria.and(CATEGORY).is(category);
        }
        if (StringUtils.isNotBlank(name)) {
            criteria.and(NAME).is(name);
        }

        query.addCriteria(criteria)
                .limit(limit);

        return mongoTemplate.find(query, ProductDocument.class);
    }
}

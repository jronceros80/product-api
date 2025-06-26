package com.products.infrastructure.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
    }

    @Test
    void shouldAddCustomPageableResolverToArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        webConfig.addArgumentResolvers(resolvers);

        assertAll("Argument resolvers validation",
                () -> assertEquals(1, resolvers.size()),
                () -> assertInstanceOf(WebConfig.CustomPageableResolver.class, resolvers.getFirst())
        );
    }

    @Test
    void shouldCreateCustomPageableResolverInstance() {
        WebConfig.CustomPageableResolver resolver = new WebConfig.CustomPageableResolver();

        assertAll("CustomPageableResolver instantiation",
                () -> assertNotNull(resolver),
                () -> assertInstanceOf(PageableHandlerMethodArgumentResolver.class, resolver)
        );
    }

    @Test
    void shouldHaveWebConfigAsSpringComponent() {
        assertNotNull(webConfig);
        assertInstanceOf(WebConfig.class, webConfig);
    }

    @Test
    void shouldCreateMultipleResolverInstances() {
        WebConfig.CustomPageableResolver resolver1 = new WebConfig.CustomPageableResolver();
        WebConfig.CustomPageableResolver resolver2 = new WebConfig.CustomPageableResolver();

        assertAll("Multiple resolver instances",
                () -> assertNotNull(resolver1),
                () -> assertNotNull(resolver2),
                () -> assertNotSame(resolver1, resolver2),
                () -> assertEquals(resolver1.getClass(), resolver2.getClass())
        );
    }

    @Test
    void shouldAddResolverToEmptyList() {
        List<HandlerMethodArgumentResolver> emptyResolvers = new ArrayList<>();

        webConfig.addArgumentResolvers(emptyResolvers);

        assertAll("Empty list resolver addition",
                () -> assertFalse(emptyResolvers.isEmpty()),
                () -> assertEquals(1, emptyResolvers.size()),
                () -> assertNotNull(emptyResolvers.getFirst())
        );
    }

    @Test
    void shouldAddResolverToExistingList() {
        List<HandlerMethodArgumentResolver> existingResolvers = new ArrayList<>();
        existingResolvers.add(new WebConfig.CustomPageableResolver());
        assertEquals(1, existingResolvers.size());

        webConfig.addArgumentResolvers(existingResolvers);

        assertAll("Existing list resolver addition",
                () -> assertEquals(2, existingResolvers.size()),
                () -> assertInstanceOf(WebConfig.CustomPageableResolver.class, existingResolvers.getFirst()),
                () -> assertInstanceOf(WebConfig.CustomPageableResolver.class, existingResolvers.get(1))
        );
    }

    @Test
    void shouldMaintainResolverTypeConsistency() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        webConfig.addArgumentResolvers(resolvers);

        HandlerMethodArgumentResolver addedResolver = resolvers.getFirst();

        assertAll("Resolver type consistency",
                () -> assertInstanceOf(WebConfig.CustomPageableResolver.class, addedResolver),
                () -> assertInstanceOf(PageableHandlerMethodArgumentResolver.class, addedResolver),
                () -> assertNotNull(addedResolver.toString())
        );
    }

} 
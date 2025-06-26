package com.products.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CustomPageableResolver());
    }

    public static class CustomPageableResolver extends PageableHandlerMethodArgumentResolver {

        public CustomPageableResolver() {
            super();
            setPageParameterName("page");
            setSizeParameterName("size");
            setOneIndexedParameters(false);
            setMaxPageSize(100);
            setFallbackPageable(PageRequest.of(0, 10));
        }

        @Override
        public Pageable resolveArgument(MethodParameter methodParameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory) {

            final String sizeParam = webRequest.getParameter("size");

            if (sizeParam != null) {
                try {
                    final int size = Integer.parseInt(sizeParam);
                    if (size <= 0) {
                        throw new IllegalArgumentException("Page size must be greater than 0");
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid page size format");
                }
            }

            return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        }
    }
}
package com.umbrastack.ecommerce.dto;

import com.umbrastack.ecommerce.domain.Product;
import jakarta.persistence.Column;

import java.util.Set;
import java.util.stream.Collectors;

public record ProductResponseDTO(Long id, String name, String description, Double price, String imgUrl, Set<CategoryResponseDTO> categories) {
    public ProductResponseDTO(Product product) {
        this(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getImgUrl(),
                product.getCategories().stream()
                        .map(CategoryResponseDTO::new)
                        .collect(Collectors.toSet()));
    }
}

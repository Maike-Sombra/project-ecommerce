package com.umbrastack.ecommerce.dto;

import com.umbrastack.ecommerce.domain.Category;

public record CategoryResponseDTO(Long id, String name) {
    public CategoryResponseDTO(Category category) {
        this(category.getId(), category.getName());
    }
}

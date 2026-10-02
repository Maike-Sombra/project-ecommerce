package com.umbrastack.ecommerce.dto;

import com.umbrastack.ecommerce.domain.User;

public record UserResponseDTO(Long id, String name, String email, String phone) {
    public UserResponseDTO(User user) {
        this(user.getId(), user.getName(), user.getEmail(), user.getPhone());
    }
}

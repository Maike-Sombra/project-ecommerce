package com.umbrastack.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDTO(
        @NotNull(message = "O ID do produto é obrigatório")
        Long productId,

        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity
) {}

package com.umbrastack.ecommerce.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDTO(
        @NotNull(message = "O ID do Cliente é obrigatório")
        Long clientId,

        @NotEmpty(message = "O pedido deve ter pelo menos um item")
        List<OrderItemRequestDTO> items
) {}

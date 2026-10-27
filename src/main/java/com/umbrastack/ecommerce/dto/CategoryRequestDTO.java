package com.umbrastack.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequestDTO(
        @NotBlank(message = "O nome da categoria é obrigatório")
        @Size(min = 3, max = 50, message = "O nome deve conter entre 3 a 50 caracteres")
        String name
) {}

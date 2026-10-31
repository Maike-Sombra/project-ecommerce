package com.umbrastack.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record ProductRequestDTO(
        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 a 100 caracteres")
        String name,

        @NotBlank(message = "A descrição é obrigatória")
        String description,

        @Positive(message = "O preço deve ser maior que zero")
        Double price,

        String imgUrl,
        //necessidade do produto de salvar a si mesmo mas também salvar a categoria na qual está associado
        Set<Long> categoryIds
) {}

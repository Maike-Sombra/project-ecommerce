package com.umbrastack.ecommerce.services;

import com.umbrastack.ecommerce.dto.CategoryResponseDTO;
import com.umbrastack.ecommerce.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDTO> findAll(){
        return categoryRepository.findAll().stream()
                .map(CategoryResponseDTO::new)
                .collect(Collectors.toList());
    }
}

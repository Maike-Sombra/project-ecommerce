package com.umbrastack.ecommerce.services;

import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.dto.CategoryResponseDTO;
import com.umbrastack.ecommerce.repositories.CategoryRepository;
import com.umbrastack.ecommerce.repositories.ProductRepository;
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
    public CategoryResponseDTO findById(Long id){
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: "+ id));
        return new CategoryResponseDTO(category);
    }
}

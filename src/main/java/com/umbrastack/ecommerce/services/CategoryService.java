package com.umbrastack.ecommerce.services;

import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.dto.CategoryRequestDTO;
import com.umbrastack.ecommerce.dto.CategoryResponseDTO;
import com.umbrastack.ecommerce.repositories.CategoryRepository;
import com.umbrastack.ecommerce.repositories.ProductRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new CategoryResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO insert(CategoryRequestDTO dto){
        Category category = new Category();
        category.setName(dto.name());
        category = categoryRepository.save(category);

        return new CategoryResponseDTO(category);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto){
        try {
            Category category = categoryRepository.getReferenceById(id);
            category.setName(dto.name());

            return new CategoryResponseDTO(category);
        } catch (EntityNotFoundException e){
            throw new ResourceNotFoundException(id);
        }
    }

    @Transactional
    public void delete(Long id){
        if (!categoryRepository.existsById(id)){
            throw new ResourceNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }
}

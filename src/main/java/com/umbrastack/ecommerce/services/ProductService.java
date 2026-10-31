package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.domain.Product;
import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.dto.ProductRequestDTO;
import com.umbrastack.ecommerce.dto.ProductResponseDTO;
import com.umbrastack.ecommerce.dto.UserResponseDTO;
import com.umbrastack.ecommerce.repositories.CategoryRepository;
import com.umbrastack.ecommerce.repositories.ProductRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll(){
        return productRepository.findAll(Sort.by("id")).stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new ProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO insert(ProductRequestDTO dto){
        Product product = new Product();
        copyDtoToEntity(dto, product);
        product = productRepository.save(product);

        return new ProductResponseDTO(product);
    }

    @Transactional
    public ProductResponseDTO update(Long id, ProductRequestDTO dto){
        try {
            Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(dto,product);

            return new ProductResponseDTO(product);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException(id);
        }
    }

    @Transactional
    public void delete(Long id){
        if (!productRepository.existsById(id)){
            throw new ResourceNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    private void copyDtoToEntity(ProductRequestDTO dto, Product entity) {
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setPrice(dto.price());
        entity.setImgUrl(dto.imgUrl());

        entity.getCategories().clear();

        if (dto.categoryIds() != null && !dto.categoryIds().isEmpty()) {
            // Executa apenas um SELECT * FROM category WHERE id IN (...)
            List<Category> categories = categoryRepository.findAllById(dto.categoryIds());

            // Validação de segurança em lote
            if (categories.size() != dto.categoryIds().size()) {
                throw new ResourceNotFoundException("Uma ou mais categorias informadas não foram encontradas.");
            }

            entity.getCategories().addAll(categories);
        }
    }
}


package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.Product;
import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.dto.ProductResponseDTO;
import com.umbrastack.ecommerce.dto.UserResponseDTO;
import com.umbrastack.ecommerce.repositories.ProductRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<ProductResponseDTO> findAll(){
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::new)
                .toList();
    }

    public ProductResponseDTO findById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new ProductResponseDTO(product);
    }
}

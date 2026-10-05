package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.dto.ProductResponseDTO;
import com.umbrastack.ecommerce.repositories.ProductRepository;
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
}

package com.umbrastack.ecommerce.services;

import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.domain.Product;
import com.umbrastack.ecommerce.dto.ProductRequestDTO;
import com.umbrastack.ecommerce.dto.ProductResponseDTO;
import com.umbrastack.ecommerce.repositories.CategoryRepository;
import com.umbrastack.ecommerce.repositories.ProductRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {
    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product = new Product();
        product.setId(10L);
        product.setName("Monitor 4K");
        product.setDescription("Monitor IPS 27 polegadas");
        product.setPrice(1800.0);
        product.getCategories().add(category);
    }

    @Test
    @DisplayName("insert deve salvar e retornar ProductResponseDTO quando todas as categorias existirem")
    void insertShouldReturnProductResponseDTOWhenCategoriesExist() {
        ProductRequestDTO request = new ProductRequestDTO(
                "Monitor 4K", "Monitor IPS 27 polegadas", 1800.0, "", Set.of(1L)
        );

        when(categoryRepository.findAllById(Set.of(1L))).thenReturn(List.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDTO result = productService.insert(request);

        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("Monitor 4K", result.name());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("insert deve lançar ResourceNotFoundException quando alguma categoria não existir")
    void insertShouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist() {
        // Envia 2 IDs, mas o mock simula que apenas 1 existe no banco
        Set<Long> categoryIds = Set.of(1L, 99L);
        ProductRequestDTO request = new ProductRequestDTO(
                "Monitor 4K", "Monitor IPS", 1800.0, "", categoryIds
        );

        when(categoryRepository.findAllById(categoryIds)).thenReturn(List.of(category));

        assertThrows(ResourceNotFoundException.class, () -> productService.insert(request));

        // Garante que o produto jamais chegou a ser salvo no banco
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("update deve lançar ResourceNotFoundException quando o ID do produto não existir")
    void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        long nonExistingId = 999L;
        ProductRequestDTO request = new ProductRequestDTO(
                "Monitor 4K", "Descrição", 1800.0, "", Set.of(1L)
        );

        when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> productService.update(nonExistingId, request));
    }

    @Test
    @DisplayName("update deve retornar ProductResponseDTO quando o ID existir")
    void updateShouldReturnProductResponseDTOWhenIdExists() {
        long existingId = 10L;
        ProductRequestDTO request = new ProductRequestDTO(
                "Monitor 4K Atualizado", "Nova descrição", 1900.0, "", Set.of(1L)
        );

        when(productRepository.getReferenceById(existingId)).thenReturn(product);
        when(categoryRepository.findAllById(Set.of(1L))).thenReturn(List.of(category));

        ProductResponseDTO result = productService.update(existingId, request);

        assertNotNull(result);
        assertEquals(existingId, result.id());
        assertEquals("Monitor 4K Atualizado", result.name());
        assertEquals(1900.0, result.price());
        verify(productRepository, times(1)).getReferenceById(existingId);
    }


}

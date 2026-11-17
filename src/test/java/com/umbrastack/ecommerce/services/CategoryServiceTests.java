package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.dto.CategoryResponseDTO;
import com.umbrastack.ecommerce.repositories.CategoryRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private Category category;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        nonExistingId = 1000L;
        category = new Category();
        category.setId(existingId);
        category.setName("Electronics");
    }

    @Test
    @DisplayName("findById deve retornar CategoryResponseDTO quando o ID existir")
    void findByIdShouldReturnCategoryResponseDTOWhenIdExists(){
        //Arrange (Configura o mock específico deste cenário)
        when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));
        //Act
        CategoryResponseDTO result = categoryService.findById(existingId);
        //Assert
        assertNotNull(result);
        assertEquals(existingId, result.id());
        assertEquals("Electronics", result.name());
        verify(categoryRepository, times(1)).findById(existingId);
    }
    @Test
    @DisplayName("findById deve lançar ResourceNotFoundException quando o ID não existir")
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        //Arrange
        when(categoryRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.findById(nonExistingId));
        verify(categoryRepository, times(1)).findById(nonExistingId);
    }
}

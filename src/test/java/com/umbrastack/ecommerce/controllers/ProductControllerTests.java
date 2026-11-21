package com.umbrastack.ecommerce.controllers;


import com.umbrastack.ecommerce.config.SecurityConfigurations;
import com.umbrastack.ecommerce.config.SecurityFilter;
import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.domain.Product;
import com.umbrastack.ecommerce.dto.ProductRequestDTO;
import com.umbrastack.ecommerce.dto.ProductResponseDTO;
import com.umbrastack.ecommerce.repositories.UserRepository;
import com.umbrastack.ecommerce.services.ProductService;
import com.umbrastack.ecommerce.services.TokenService;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import({SecurityConfigurations.class, SecurityFilter.class})
@ActiveProfiles("test")
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    private ProductResponseDTO productResponseDTO;

    @BeforeEach
    void setUp() {
        //Category category = new Category(1L, "Electronics");
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        Product product = new Product();
        product.setId(1L);
        product.setName("Monitor 4K");
        product.setDescription("Monitor IPS 27 polegadas");
        product.setPrice(1800.0);
        product.setImgUrl("");
        product.getCategories().add(category);

        productResponseDTO = new ProductResponseDTO(product);
    }

    @Test
    @DisplayName("GET /products deve retornar 200 OK com estrutura paginada")
    void findAllShouldReturnPagedProducts() throws Exception {
        PageImpl<ProductResponseDTO> page = new PageImpl<>(List.of(productResponseDTO));
        when(productService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/products")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Monitor 4K"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /products/{id} deve retornar 404 Not Found no padrão RFC 7807 quando ID não existir")
    void findByIdShouldReturn404ProblemDetailWhenIdDoesNotExist() throws Exception {
        when(productService.findById(999L)).thenThrow(new ResourceNotFoundException(999L));

        mockMvc.perform(get("/products/{id}", 999L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products deve retornar 201 Created e header Location quando payload for válido")
    void insertShouldReturn201CreatedWithLocationHeader() throws Exception {
        when(productService.insert(any(ProductRequestDTO.class))).thenReturn(productResponseDTO);

        String validJson = """
                {
                  "name": "Monitor 4K",
                  "description": "Monitor IPS 27 polegadas",
                  "price": 1800.0,
                  "imgUrl": "",
                  "categoryIds": [1]
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Monitor 4K"));
    }

    @Test
    @WithMockUser(roles = "CLIENT")
    @DisplayName("POST /products deve retornar 403 Forbidden quando usuário não for ADMIN")
    void insertShouldReturn403ForbiddenWhenUserIsNotAdmin() throws Exception {
        String validJson = """
                {
                  "name": "Monitor 4K",
                  "description": "Monitor IPS 27 polegadas",
                  "price": 1800.0,
                  "imgUrl": "",
                  "categoryIds": [1]
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isForbidden());

        verify(productService, never()).insert(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /products deve retornar 422 Unprocessable Entity com mapa de erros quando validação falhar")
    void insertShouldReturn422ProblemDetailWhenValidationFails() throws Exception {
        String invalidJson = """
                {
                  "name": "",
                  "description": "",
                  "price": -50.0,
                  "categoryIds": [1]
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.title").value("Validation Error"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.description").exists())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id} deve retornar 204 No Content quando exclusão for bem-sucedida")
    void deleteShouldReturn204WhenIdExistsAndHasNoDependencies() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /products/{id} deve retornar 409 Conflict no padrão RFC 7807 quando houver violação de integridade")
    void deleteShouldReturn409ProblemDetailWhenIntegrityViolationOccurs() throws Exception {
        doThrow(DataIntegrityViolationException.class).when(productService).delete(eq(1L));

        mockMvc.perform(delete("/products/{id}", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Database Integrity Violation"));
    }
}

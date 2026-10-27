package com.umbrastack.ecommerce.controllers;


import com.umbrastack.ecommerce.domain.Category;
import com.umbrastack.ecommerce.dto.CategoryRequestDTO;
import com.umbrastack.ecommerce.dto.CategoryResponseDTO;
import com.umbrastack.ecommerce.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> findAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CategoryResponseDTO> findById (@PathVariable Long id){
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> insert(@Valid @RequestBody CategoryRequestDTO dto){
        CategoryResponseDTO newDto = categoryService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO dto){
        return ResponseEntity.ok(categoryService.update(id,dto));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

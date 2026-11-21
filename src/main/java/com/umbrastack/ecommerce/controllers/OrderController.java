package com.umbrastack.ecommerce.controllers;

import com.umbrastack.ecommerce.dto.OrderRequestDTO;
import com.umbrastack.ecommerce.dto.OrderResponseDTO;
import com.umbrastack.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> findAll(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    ){
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<OrderResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> insert(@Valid @RequestBody OrderRequestDTO dto){
        OrderResponseDTO newDto = orderService.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDto.id()).toUri();
        return ResponseEntity.created(uri).body(newDto);
    }

    @PutMapping(value = "/{id}/pay")
    public ResponseEntity<OrderResponseDTO> registerPayment(@PathVariable Long id){
        OrderResponseDTO dto = orderService.registerPayment(id);
        return ResponseEntity.ok(dto);
    }
}

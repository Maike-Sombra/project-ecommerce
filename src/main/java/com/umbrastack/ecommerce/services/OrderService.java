package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.Order;
import com.umbrastack.ecommerce.dto.OrderResponseDTO;
import com.umbrastack.ecommerce.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public List<OrderResponseDTO> findAll(){
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public OrderResponseDTO findById(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado com o ID: "+id));
        return new OrderResponseDTO(order);
    }
}

package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.*;
import com.umbrastack.ecommerce.domain.enums.OrderStatus;
import com.umbrastack.ecommerce.dto.OrderItemRequestDTO;
import com.umbrastack.ecommerce.dto.OrderRequestDTO;
import com.umbrastack.ecommerce.dto.OrderResponseDTO;
import com.umbrastack.ecommerce.repositories.OrderItemRepository;
import com.umbrastack.ecommerce.repositories.OrderRepository;
import com.umbrastack.ecommerce.repositories.ProductRepository;
import com.umbrastack.ecommerce.repositories.UserRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> findAll(){
        return orderRepository.findAll(Sort.by("id")).stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO findById(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new OrderResponseDTO(order);
    }

    // 1. A ROTA DE CHECKOUT (COMPRA)
    @Transactional
    public OrderResponseDTO insert(OrderRequestDTO dto){
        Order order = new Order();
        order.setMoment(Instant.now());
        order.setOrderStatus(OrderStatus.WAITING_PAYMENT);

        //busca real para garantir a integridade antes de associar ao pedido
        User client = userRepository.findById(dto.clientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cliente de ID "+ dto.clientId() +" não encontrado"
                        ));
        order.setClient(client);

        //salvar o pedido pra gerar o ID
        order = orderRepository.save(order);

        for (OrderItemRequestDTO itemDto : dto.items()){
            Product product = productRepository.findById(itemDto.productId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Produto de ID "+ itemDto.productId() +" não encontrado"
                            ));
            OrderItem orderItem = new OrderItem(order, product, itemDto.quantity(), product.getPrice());
            orderItemRepository.save(orderItem);
            order.getItems().add(orderItem);
        }
        return new OrderResponseDTO(order);
    }

    @Transactional
    public OrderResponseDTO registerPayment(Long id){
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        if (order.getOrderStatus() != OrderStatus.WAITING_PAYMENT){
            throw new IllegalStateException("Operação Inválida: Este pedido já foi pago ou cancelado.");
        }

        Payment payment = new Payment(null,Instant.now(),order);
        order.setPayment(payment);
        order.setOrderStatus(OrderStatus.PAID);

        //salvar e dar flush força o hibernate a sicronizar a árvore de objetos e calcular o ID do Payment
        order = orderRepository.saveAndFlush(order);

        return new OrderResponseDTO(order);
    }
}

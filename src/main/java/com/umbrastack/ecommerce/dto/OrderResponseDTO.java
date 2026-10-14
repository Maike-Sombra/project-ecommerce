package com.umbrastack.ecommerce.dto;

import com.umbrastack.ecommerce.domain.Order;
import com.umbrastack.ecommerce.domain.OrderItem;
import com.umbrastack.ecommerce.domain.Payment;
import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.domain.enums.OrderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record OrderResponseDTO(
        Long id,
        Instant moment,
        String orderStatus,
        UserResponseDTO client,
        PaymentResponseDTO payment,
        Set<OrderItemResponseDTO> items,
        Double total
) {
    public OrderResponseDTO(Order order){
        this(
                order.getId(),
                order.getMoment(),
                order.getOrderStatus() != null
                        ? order.getOrderStatus().name()
                        : null,
                new UserResponseDTO(order.getClient()),
                order.getPayment() != null
                        ? new PaymentResponseDTO(order.getPayment())
                        : null,
                order.getItems().stream()
                        .map(OrderItemResponseDTO::new)
                        .collect(Collectors.toSet()),
                order.getItems().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum()
        );
    }
}
//private Long id;
//private Instant moment;
//@Enumerated(EnumType.STRING)
//private OrderStatus orderStatus;
//
//@ManyToOne
//@JoinColumn(name = "client_id")
//private User client;
//
////o que acontecer com o pedido, o pagamento também será persistido, removido, atualizado
//@OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
//private Payment payment;
//
//@OneToMany(mappedBy = "id.order")
//private Set<OrderItem> items = new HashSet<>();

package com.umbrastack.ecommerce.dto;

import com.umbrastack.ecommerce.domain.OrderItem;

public record OrderItemResponseDTO(
        Long productId,
        String productName,
        Double price,
        Integer quantity,
        Double subTotal
) {
    public OrderItemResponseDTO(OrderItem orderItem){
        this(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getPrice() * orderItem.getQuantity()
        );
    }
}

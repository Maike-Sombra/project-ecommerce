package com.umbrastack.ecommerce.dto;

import com.umbrastack.ecommerce.domain.Payment;

import java.time.Instant;

public record PaymentResponseDTO(Long id, Instant moment) {
    public PaymentResponseDTO(Payment payment){
        this(payment.getId(), payment.getMoment());
    }
}

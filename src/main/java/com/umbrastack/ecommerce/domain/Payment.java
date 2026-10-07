package com.umbrastack.ecommerce.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "tb_payment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Payment implements Serializable {
    @Id
    private Long id;//sem generatevalue pois o id do pagamento é o mesmo do pedido

    private Instant moment;

    @OneToOne
    @MapsId //o id do pagamento é o mesmo do pedido
    private Order order;
}

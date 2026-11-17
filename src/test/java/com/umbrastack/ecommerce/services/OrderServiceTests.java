package com.umbrastack.ecommerce.services;


import com.umbrastack.ecommerce.domain.Order;
import com.umbrastack.ecommerce.domain.OrderItem;
import com.umbrastack.ecommerce.domain.Product;
import com.umbrastack.ecommerce.domain.User;
import com.umbrastack.ecommerce.domain.enums.OrderStatus;
import com.umbrastack.ecommerce.dto.OrderItemRequestDTO;
import com.umbrastack.ecommerce.dto.OrderRequestDTO;
import com.umbrastack.ecommerce.dto.OrderResponseDTO;
import com.umbrastack.ecommerce.repositories.OrderItemRepository;
import com.umbrastack.ecommerce.repositories.OrderRepository;
import com.umbrastack.ecommerce.repositories.ProductRepository;
import com.umbrastack.ecommerce.repositories.UserRepository;
import com.umbrastack.ecommerce.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTests {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    private User client;
    private Product product1;
    private Product product2;
    private Order waitingOrder;
    private Order paidOrder;

    @BeforeEach
    void setUp() {
        client = new User(1L, "Maria Brown", "maria@gmail.com", "988888888", "123456");

        product1 = new Product();
        product1.setId(1L);
        product1.setName("Smart TV");
        product1.setPrice(2000.0);

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse Gamer");
        product2.setPrice(150.0);

        waitingOrder = new Order();
        waitingOrder.setId(10L);
        waitingOrder.setMoment(Instant.now());
        waitingOrder.setOrderStatus(OrderStatus.WAITING_PAYMENT);
        waitingOrder.setClient(client);

        paidOrder = new Order();
        paidOrder.setId(20L);
        paidOrder.setMoment(Instant.now());
        paidOrder.setOrderStatus(OrderStatus.PAID);
        paidOrder.setClient(client);
    }

    @Test
    @DisplayName("insert deve criar pedido com status WAITING_PAYMENT e calcular o total corretamente")
    void insertShouldCreateOrderWithCalculatedTotalAndWaitingPaymentStatus() {
        OrderRequestDTO request = new OrderRequestDTO(1L, List.of(
                new OrderItemRequestDTO(1L, 1),
                new OrderItemRequestDTO(2L, 2)
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        OrderResponseDTO result = orderService.insert(request);

        assertNotNull(result);
        assertEquals(OrderStatus.WAITING_PAYMENT.name(), result.orderStatus());
        assertEquals(2300.0, result.total());
        assertEquals(2, result.items().size());
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("insert deve lançar ResourceNotFoundException quando um produto do carrinho não existir")
    void insertShouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        OrderRequestDTO request = new OrderRequestDTO(1L, List.of(
                new OrderItemRequestDTO(999L, 1)
        ));

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(orderRepository.save(any(Order.class))).thenReturn(waitingOrder);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.insert(request));
        verify(orderItemRepository, never()).save(any(OrderItem.class));
    }

    @Test
    @DisplayName("registerPayment deve alterar status para PAID e anexar pagamento quando aguardando pagamento")
    void registerPaymentShouldUpdateStatusToPaidWhenOrderIsWaitingPayment() {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(waitingOrder));
        when(orderRepository.saveAndFlush(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponseDTO result = orderService.registerPayment(10L);

        assertNotNull(result);
        assertEquals(OrderStatus.PAID.name(), result.orderStatus());
        assertNotNull(result.payment());
        verify(orderRepository, times(1)).saveAndFlush(waitingOrder);
    }

    @Test
    @DisplayName("registerPayment deve lançar IllegalStateException ao tentar pagar pedido já pago")
    void registerPaymentShouldThrowIllegalStateExceptionWhenOrderIsAlreadyPaid() {
        when(orderRepository.findById(20L)).thenReturn(Optional.of(paidOrder));

        assertThrows(IllegalStateException.class, () -> orderService.registerPayment(20L));
        verify(orderRepository, never()).saveAndFlush(any(Order.class));
    }
}

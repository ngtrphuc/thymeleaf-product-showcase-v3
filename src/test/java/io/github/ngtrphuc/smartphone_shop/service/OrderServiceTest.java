package io.github.ngtrphuc.smartphone_shop.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.ngtrphuc.smartphone_shop.model.Order;
import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.model.CartItem;
import io.github.ngtrphuc.smartphone_shop.repository.OrderRepository;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, productRepository);
    }

    @Test
    void updateStatus_shouldThrowWhenOrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderValidationException.class, () -> orderService.updateStatus(99L, "processing"));
        verify(orderRepository).findById(99L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void updateStatus_shouldThrowForUnsupportedStatus() {
        Order order = new Order();
        order.setId(7L);
        order.setStatus("pending");
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));

        assertThrows(OrderValidationException.class, () -> orderService.updateStatus(7L, "invalid_status"));
        verify(orderRepository).findById(7L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    void createOrder_shouldRejectInvalidPhoneFormat() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Phone A");
        product.setStock(5);

        CartItem item = new CartItem(1L, "Phone A", 100.0, 1);
        when(productRepository.findAllByIdInForUpdate(anyCollection())).thenReturn(List.of(product));

        assertThrows(OrderValidationException.class, () ->
                orderService.createOrder("user@example.com", "John Doe", "abc123", "Tokyo", List.of(item), 100.0));
    }
}

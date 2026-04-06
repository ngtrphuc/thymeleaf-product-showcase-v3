package io.github.ngtrphuc.smartphone_shop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;

import io.github.ngtrphuc.smartphone_shop.model.CartItem;
import io.github.ngtrphuc.smartphone_shop.model.CartItemEntity;
import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.CartItemRepository;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartItemRepository, productRepository);
    }

    @Test
    void mergeSessionCartToDb_shouldClampByStockAndSkipOutOfStock() {
        MockHttpSession session = new MockHttpSession();
        List<CartItem> sessionCart = new ArrayList<>();
        sessionCart.add(new CartItem(1L, "Phone A", 100.0, 5));
        sessionCart.add(new CartItem(2L, "Phone B", 200.0, 2));
        session.setAttribute("cart", sessionCart);

        Product inStock = new Product();
        inStock.setId(1L);
        inStock.setStock(3);

        Product outOfStock = new Product();
        outOfStock.setId(2L);
        outOfStock.setStock(0);

        CartItemEntity existing = new CartItemEntity("user@example.com", 1L, 1);

        when(productRepository.findAllByIdIn(any())).thenReturn(List.of(inStock, outOfStock));
        when(cartItemRepository.findByUserEmail("user@example.com")).thenReturn(List.of(existing));

        cartService.mergeSessionCartToDb(session, "user@example.com");

        verify(cartItemRepository).save(existing);
        assertEquals(Long.valueOf(1L), existing.getProductId());
        assertEquals(3, existing.getQuantity());
        assertNull(session.getAttribute("cart"));
        assertEquals(3, session.getAttribute("cartCount"));
    }

    @Test
    void increaseItem_shouldDoNothingWhenProductMissingOrNoStock() {
        MockHttpSession session = new MockHttpSession();
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        cartService.increaseItem("user@example.com", session, 10L);

        verify(cartItemRepository, never()).findByUserEmailAndProductId(any(), any());
    }

    @Test
    void getDbCart_shouldRemoveOrphanedItemsWhenProductNoLongerExists() {
        CartItemEntity orphan = new CartItemEntity("user@example.com", 99L, 2);
        when(cartItemRepository.findByUserEmail("user@example.com")).thenReturn(List.of(orphan));
        when(productRepository.findAllByIdIn(List.of(99L))).thenReturn(List.of());

        List<CartItem> cart = cartService.getDbCart("user@example.com");

        assertTrue(cart.isEmpty());
        Object deletedArgument = org.mockito.Mockito.mockingDetails(cartItemRepository).getInvocations().stream()
                .filter(invocation -> invocation.getMethod().getName().equals("deleteAll"))
                .map(invocation -> invocation.getArgument(0))
                .findFirst()
                .orElseThrow();
        Iterable<?> deletedItems = assertInstanceOf(Iterable.class, deletedArgument);
        List<Object> deletedList = new ArrayList<>();
        deletedItems.forEach(deletedList::add);
        assertEquals(1, deletedList.size());
        assertSame(orphan, deletedList.getFirst());
    }
}

package io.github.ngtrphuc.smartphone_shop.controller.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CompareControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Test
    void add_shouldKeepOnlyThreeProductsInSession() {
        CompareController controller = new CompareController(productRepository);
        MockHttpSession session = new MockHttpSession();
        RedirectAttributesModelMap ra = new RedirectAttributesModelMap();

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.existsById(2L)).thenReturn(true);
        when(productRepository.existsById(3L)).thenReturn(true);
        when(productRepository.existsById(4L)).thenReturn(true);

        controller.add(1L, "/", session, ra);
        controller.add(2L, "/", session, ra);
        controller.add(3L, "/", session, ra);
        controller.add(4L, "/", session, ra);

        Object compareIds = session.getAttribute("compareIds");
        List<?> rawIds = assertInstanceOf(List.class, compareIds);

        assertEquals(3, rawIds.size());
        assertEquals(List.of(1L, 2L, 3L), rawIds);
    }

    @Test
    void comparePage_shouldRenderProductsInSessionOrder() {
        CompareController controller = new CompareController(productRepository);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("compareIds", new ArrayList<>(List.of(3L, 1L)));

        Product first = new Product();
        first.setId(1L);
        first.setName("Model 1");

        Product third = new Product();
        third.setId(3L);
        third.setName("Model 3");

        when(productRepository.findAllByIdIn(List.of(3L, 1L))).thenReturn(List.of(first, third));

        Model model = new ExtendedModelMap();
        String view = controller.comparePage(session, model);

        Object products = model.getAttribute("products");
        List<?> rawProducts = assertInstanceOf(List.class, products);
        Product firstOrdered = assertInstanceOf(Product.class, rawProducts.get(0));
        Product secondOrdered = assertInstanceOf(Product.class, rawProducts.get(1));

        assertEquals("compare", view);
        assertEquals(List.of(3L, 1L), List.of(firstOrdered.getId(), secondOrdered.getId()));
    }

    @Test
    void remove_shouldRemoveByIdValue() {
        CompareController controller = new CompareController(productRepository);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("compareIds", new ArrayList<>(List.of(7L, 9L)));

        String redirect = controller.remove(9L, "/compare", session);

        Object compareIds = session.getAttribute("compareIds");
        List<?> rawIds = assertInstanceOf(List.class, compareIds);

        assertEquals("redirect:/compare", redirect);
        assertEquals(List.of(7L), rawIds);
    }
}

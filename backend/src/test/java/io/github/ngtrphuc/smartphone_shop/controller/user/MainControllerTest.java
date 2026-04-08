package io.github.ngtrphuc.smartphone_shop.controller.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import io.github.ngtrphuc.smartphone_shop.service.WishlistService;

@ExtendWith(MockitoExtension.class)
class MainControllerTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WishlistService wishlistService;

    @Test
    void index_shouldFilterAppleBrandFromAppleIphoneName() {
        MainController mainController = new MainController(productRepository, wishlistService);

        Product iphone = new Product();
        iphone.setId(1L);
        iphone.setName("Apple iPhone 17 Pro");
        iphone.setPrice(1000.0);

        Product galaxy = new Product();
        galaxy.setId(2L);
        galaxy.setName("Galaxy S26 Ultra");
        galaxy.setPrice(1200.0);

        when(productRepository.findAllWithFilters(null, null, null)).thenReturn(List.of(iphone, galaxy));

        Model model = new ExtendedModelMap();
        String view = mainController.index(null, null, "Apple",
                null, null, null, null, null, null, null, 9, 0, null, model);

        Object productsAttribute = model.getAttribute("products");
        List<?> rawProducts = assertInstanceOf(List.class, productsAttribute);
        Product filteredProduct = assertInstanceOf(Product.class, rawProducts.getFirst());

        assertEquals("index", view);
        assertEquals(1, rawProducts.size());
        assertEquals("Apple iPhone 17 Pro", filteredProduct.getName());
    }
}

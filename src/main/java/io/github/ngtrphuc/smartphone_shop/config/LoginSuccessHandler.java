package io.github.ngtrphuc.smartphone_shop.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import io.github.ngtrphuc.smartphone_shop.service.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CartService cartService;

    public LoginSuccessHandler(CartService cartService) {
        this.cartService = cartService;
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        // Merge session cart → DB ngay sau khi login thành công
        cartService.mergeSessionCartToDb(session, authentication.getName());
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
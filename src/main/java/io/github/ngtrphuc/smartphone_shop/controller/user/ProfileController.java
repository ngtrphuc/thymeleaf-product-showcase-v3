package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.ngtrphuc.smartphone_shop.model.CartItem;
import io.github.ngtrphuc.smartphone_shop.model.Order;
import io.github.ngtrphuc.smartphone_shop.model.User;
import io.github.ngtrphuc.smartphone_shop.repository.UserRepository;
import io.github.ngtrphuc.smartphone_shop.service.CartService;
import io.github.ngtrphuc.smartphone_shop.service.OrderService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final OrderService orderService;
    private final CartService cartService;

    public ProfileController(UserRepository userRepository, OrderService orderService,
            CartService cartService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping
    public String profile(Authentication auth, HttpSession session, Model model) {
        String email = auth.getName();
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow();
        List<Order> allOrders = orderService.getOrdersByUser(email);
        List<Order> deliveredOrders = allOrders.stream()
                .filter(o -> "delivered".equals(o.getStatus())).toList();
        List<Order> pendingOrders = allOrders.stream()
                .filter(o -> !"delivered".equals(o.getStatus())
                && !"cancelled".equals(o.getStatus())).toList();
        List<CartItem> cartItems = cartService.getCart(email, session);
        model.addAttribute("user", user);
        model.addAttribute("deliveredOrders", deliveredOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("cartItems", cartItems);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(Authentication auth,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam String defaultAddress,
            RedirectAttributes ra) {
        String normalizedFullName = normalizeInline(fullName);
        if (normalizedFullName.isBlank()) {
            ra.addFlashAttribute("toast", "Full name cannot be empty.");
            return "redirect:/profile";
        }

        userRepository.findByEmailIgnoreCase(auth.getName()).ifPresent(user -> {
            user.setFullName(normalizedFullName);
            user.setPhoneNumber(normalizeInline(phoneNumber));
            user.setDefaultAddress(normalizeInline(defaultAddress));
            userRepository.save(user);
        });
        ra.addFlashAttribute("toast", "Profile updated!");
        return "redirect:/profile";
    }

    private String normalizeInline(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}

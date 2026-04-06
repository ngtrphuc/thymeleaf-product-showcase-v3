package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.ngtrphuc.smartphone_shop.model.CartItem;
import io.github.ngtrphuc.smartphone_shop.repository.UserRepository;
import io.github.ngtrphuc.smartphone_shop.service.CartService;
import io.github.ngtrphuc.smartphone_shop.service.OrderValidationException;
import io.github.ngtrphuc.smartphone_shop.service.OrderService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+()\\-\\s]{6,30}$");
    private static final int MAX_NAME_LENGTH = 120;
    private static final int MAX_PHONE_LENGTH = 30;
    private static final int MAX_ADDRESS_LENGTH = 255;

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, OrderService orderService,
            UserRepository userRepository) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private String getEmail(Authentication auth) {
        return (auth != null) ? auth.getName() : null;
    }

    @GetMapping
    public String viewCart(Authentication auth, HttpSession session, Model model) {
        String email = getEmail(auth);
        List<CartItem> cart = cartService.getCart(email, session);
        cartService.syncCartCount(session, email);
        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", cartService.calculateTotal(cart));
        return "cart";
    }

    @PostMapping("/add")
    public String add(@RequestParam long id, Authentication auth,
            HttpSession session, RedirectAttributes ra) {
        CartService.AddItemResult result = cartService.addItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        String toast = switch (result) {
            case ADDED -> "Added to cart successfully.";
            case LIMIT_REACHED -> "You've already added the maximum available stock for this product.";
            case UNAVAILABLE -> "This product is unavailable right now.";
        };
        ra.addFlashAttribute("toast", toast);
        return "redirect:/product/" + id;
    }

    @PostMapping("/increase/{id}")
    public String increase(@PathVariable long id, Authentication auth, HttpSession session) {
        cartService.increaseItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        return "redirect:/cart";
    }

    @PostMapping("/decrease/{id}")
    public String decrease(@PathVariable long id, Authentication auth, HttpSession session) {
        cartService.decreaseItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        return "redirect:/cart";
    }

    @PostMapping("/remove/{id}")
    public String remove(@PathVariable long id, Authentication auth, HttpSession session) {
        cartService.removeItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        return "redirect:/cart";
    }

    @GetMapping("/shipping")
    public String shipping(Authentication auth, HttpSession session, Model model) {
        if (cartService.getCart(getEmail(auth), session).isEmpty()) {
            return "redirect:/cart";
        }
        String email = getEmail(auth);
        if (email != null && !"anonymousUser".equals(email)) {
            userRepository.findByEmailIgnoreCase(email).ifPresent(u -> model.addAttribute("user", u));
        }
        return "shipping";
    }

    @PostMapping("/process-shipping")
    public String processShipping(@RequestParam String customerName,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String addressOption,
            @RequestParam(required = false) String savedAddress,
            @RequestParam(required = false) String address,
            HttpSession session,
            RedirectAttributes ra) {
        String normalizedName = normalizeInline(customerName);
        String normalizedPhone = normalizeInline(phoneNumber);
        String normalizedSavedAddress = normalizeInline(savedAddress);
        String normalizedAddress = normalizeInline(address);
        String finalAddress = "new".equals(addressOption) ? normalizedAddress
                : (!normalizedSavedAddress.isBlank() ? normalizedSavedAddress : normalizedAddress);
        if (normalizedName.isBlank() || normalizedPhone.isBlank() || finalAddress.isBlank()) {
            ra.addFlashAttribute("toast", "Please complete your shipping details.");
            return "redirect:/cart/shipping";
        }
        if (normalizedName.length() > MAX_NAME_LENGTH) {
            ra.addFlashAttribute("toast", "Full name is too long.");
            return "redirect:/cart/shipping";
        }
        if (normalizedPhone.length() > MAX_PHONE_LENGTH || !PHONE_PATTERN.matcher(normalizedPhone).matches()) {
            ra.addFlashAttribute("toast", "Phone number format is invalid.");
            return "redirect:/cart/shipping";
        }
        if (finalAddress.length() > MAX_ADDRESS_LENGTH) {
            ra.addFlashAttribute("toast", "Shipping address is too long.");
            return "redirect:/cart/shipping";
        }
        session.setAttribute("name", normalizedName);
        session.setAttribute("phone", normalizedPhone);
        session.setAttribute("address", finalAddress);
        return "redirect:/cart/checkout";
    }

    @GetMapping("/checkout")
    public String checkout(Authentication auth, HttpSession session, Model model) {
        String email = getEmail(auth);
        List<CartItem> cart = cartService.getCart(email, session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }
        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", cartService.calculateTotal(cart));
        model.addAttribute("count", cart.stream().mapToInt(CartItem::getQuantity).sum());
        return "checkout";
    }

    @PostMapping("/confirm")
    public String confirm(Authentication auth, HttpSession session, RedirectAttributes ra) {
        String name = (String) session.getAttribute("name");
        String phone = (String) session.getAttribute("phone");
        String address = (String) session.getAttribute("address");
        String email = getEmail(auth);
        List<CartItem> cart = cartService.getCart(email, session);

        if (email == null || "anonymousUser".equals(email)) {
            ra.addFlashAttribute("toast", "Please log in before placing an order.");
            return "redirect:/login";
        }

        if (cart.isEmpty() || name == null || phone == null || address == null) {
            return "redirect:/cart/shipping";
        }

        try {
            orderService.createOrder(email, name, phone, address, cart);
            cartService.clearCart(email, session);
            session.removeAttribute("name");
            session.removeAttribute("phone");
            session.removeAttribute("address");

            ra.addFlashAttribute("orderSuccess", true);
            return "redirect:/cart/success";
        } catch (OrderValidationException ex) {
            ra.addFlashAttribute("toast", ex.getMessage());
            return "redirect:/cart/checkout";
        }
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }

    private String normalizeInline(String value) {
        return value == null ? "" : value.trim().replaceAll("\\s+", " ");
    }
}

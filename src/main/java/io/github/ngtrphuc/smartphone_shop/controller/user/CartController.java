package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.List;

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
import io.github.ngtrphuc.smartphone_shop.service.OrderService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart")
public class CartController {

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

    @GetMapping("/add")
    public String add(@RequestParam long id, Authentication auth,
            HttpSession session, RedirectAttributes ra) {
        cartService.addItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        ra.addFlashAttribute("toast", "Added to cart! ðŸ›’");
        return "redirect:/product/" + id;
    }

    @GetMapping("/increase/{id}")
    public String increase(@PathVariable long id, Authentication auth, HttpSession session) {
        cartService.increaseItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        return "redirect:/cart";
    }

    @GetMapping("/decrease/{id}")
    public String decrease(@PathVariable long id, Authentication auth, HttpSession session) {
        cartService.decreaseItem(getEmail(auth), session, id);
        cartService.syncCartCount(session, getEmail(auth));
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
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
        if (auth != null) {
            userRepository.findByEmail(auth.getName()).ifPresent(u -> model.addAttribute("user", u));
        }
        return "shipping";
    }

    @PostMapping("/process-shipping")
    public String processShipping(@RequestParam String customerName,
            @RequestParam String phoneNumber,
            @RequestParam(required = false) String addressOption,
            @RequestParam(required = false) String savedAddress,
            @RequestParam(required = false) String address,
            HttpSession session) {
        String finalAddress = "new".equals(addressOption) ? address
                : (savedAddress != null && !savedAddress.isBlank() ? savedAddress : address);
        if (customerName.isBlank() || phoneNumber.isBlank()
                || finalAddress == null || finalAddress.isBlank()) {
            return "redirect:/cart/shipping";
        }
        session.setAttribute("name", customerName);
        session.setAttribute("phone", phoneNumber);
        session.setAttribute("address", finalAddress);
        return "redirect:/cart/checkout";
    }

    @GetMapping("/checkout")
    public String checkout(Authentication auth, HttpSession session, Model model) {
        String email = getEmail(auth);
        List<CartItem> cart = cartService.getCart(email, session);
        if (cart.isEmpty()) return "redirect:/cart";
        model.addAttribute("cart", cart);
        model.addAttribute("totalAmount", cartService.calculateTotal(cart));
        model.addAttribute("count", cart.stream().mapToInt(CartItem::getQuantity).sum());
        return "checkout";
    }

    @PostMapping("/confirm")
    public String confirm(Authentication auth, HttpSession session, RedirectAttributes ra) {
        String name    = (String) session.getAttribute("name");
        String phone   = (String) session.getAttribute("phone");
        String address = (String) session.getAttribute("address");
        String email   = getEmail(auth);
        List<CartItem> cart = cartService.getCart(email, session);

        if (cart.isEmpty() || name == null || phone == null || address == null) {
            return "redirect:/cart/shipping";
        }

        orderService.createOrder(email, name, phone, address, cart, cartService.calculateTotal(cart));
        cartService.clearCart(email, session);
        session.removeAttribute("name");
        session.removeAttribute("phone");
        session.removeAttribute("address");

        ra.addFlashAttribute("orderSuccess", true);
        return "redirect:/cart/success";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
}


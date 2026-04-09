package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/compare")
public class CompareController {

    private static final String SESSION_KEY = "compareIds";
    private static final int MAX_COMPARE = 3;

    private final ProductRepository productRepository;

    public CompareController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public String comparePage(HttpSession session, Model model) {
        List<Long> ids = getIds(session);
        if (ids.isEmpty()) {
            model.addAttribute("products", List.of());
            return "compare";
        }

        List<Product> products = productRepository.findAllByIdIn(ids);
        Map<Long, Product> productsById = new HashMap<>();
        for (Product product : products) {
            if (product.getId() != null) {
                productsById.put(product.getId(), product);
            }
        }

        List<Product> ordered = ids.stream()
                .map(productsById::get)
                .filter(Objects::nonNull)
                .toList();

        if (ordered.size() != ids.size()) {
            List<Long> cleanedIds = ordered.stream()
                    .map(Product::getId)
                    .filter(Objects::nonNull)
                    .toList();
            session.setAttribute(SESSION_KEY, new ArrayList<>(cleanedIds));
        }

        model.addAttribute("products", ordered);
        Set<Long> selectedIds = new java.util.HashSet<>(ids);
        List<Product> availableProducts = productRepository.findAll().stream()
                .filter(product -> product.getId() != null && !selectedIds.contains(product.getId()))
                .toList();
        model.addAttribute("availableProducts", availableProducts);
        return "compare";
    }

    @PostMapping("/add")
    public String add(
            @RequestParam long id,
            @RequestParam(required = false) String redirect,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        List<Long> ids = getIds(session);
        if (!productRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("toast", "Product not found.");
        } else if (ids.contains(id)) {
            redirectAttributes.addFlashAttribute("toast", "This product is already in compare list.");
        } else if (ids.size() >= MAX_COMPARE) {
            redirectAttributes.addFlashAttribute("toast", "You can compare up to " + MAX_COMPARE + " products.");
        } else {
            ids.add(id);
            session.setAttribute(SESSION_KEY, ids);
            redirectAttributes.addFlashAttribute("toast", "Added to compare list.");
        }
        return "redirect:" + normalizeRedirect(redirect);
    }

    @PostMapping("/remove")
    public String remove(
            @RequestParam long id,
            @RequestParam(required = false) String redirect,
            HttpSession session) {
        List<Long> ids = getIds(session);
        ids.remove(Long.valueOf(id));
        session.setAttribute(SESSION_KEY, ids);
        return "redirect:" + normalizeRedirect(redirect);
    }

    @PostMapping("/clear")
    public String clear(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
        return "redirect:/compare";
    }

    @SuppressWarnings("unchecked")
    private List<Long> getIds(HttpSession session) {
        Object obj = session.getAttribute(SESSION_KEY);
        if (obj instanceof List<?> rawList && rawList.stream().allMatch(Long.class::isInstance)) {
            return new ArrayList<>((List<Long>) rawList);
        }
        return new ArrayList<>();
    }

    private String normalizeRedirect(String redirect) {
        if (redirect == null || redirect.isBlank() || !redirect.startsWith("/") || redirect.startsWith("//")) {
            return "/";
        }
        return redirect.trim();
    }
}

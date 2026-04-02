package io.github.ngtrphuc.smartphone_shop.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import io.github.ngtrphuc.smartphone_shop.service.OrderService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public AdminController(ProductRepository productRepository, OrderService orderService) {
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalItemsSold", orderService.getTotalItemsSold());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("recentOrders", orderService.getRecentOrders(10));
        model.addAttribute("shopname", "Smartphone Shop");
        return "admin/dashboard";
    }

    @GetMapping("/access-denied-admin")
    public String accessDeniedAdmin(Model model) {
        model.addAttribute("shopname", "Smartphone Shop");
        return "error/access-denied-admin";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productRepository.findAll());
        model.addAttribute("shopname", "Smartphone Shop");
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("shopname", "Smartphone Shop");
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        if (id == null) return "redirect:/admin/products";
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) return "redirect:/admin/products";
        model.addAttribute("product", p);
        model.addAttribute("shopname", "Smartphone Shop");
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes ra) {
        boolean isNew = (product.getId() == null);
        productRepository.save(product);
        ra.addFlashAttribute("toast", isNew ? "Product added successfully!" : "Product updated successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes ra) {
        if (id != null) {
            productRepository.deleteById(id);
            ra.addFlashAttribute("toast", "Product deleted successfully.");
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("shopname", "Smartphone Shop");
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status,
            RedirectAttributes ra) {
        orderService.updateStatus(id, status);
        ra.addFlashAttribute("toast", "Status update successful.");
        return "redirect:/admin/orders";
    }
}
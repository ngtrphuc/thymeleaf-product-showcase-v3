package io.github.ngtrphuc.smartphone_shop.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.OrderRepository;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import io.github.ngtrphuc.smartphone_shop.service.OrderService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int ADMIN_PAGE_SIZE = 20;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public AdminController(ProductRepository productRepository, OrderRepository orderRepository, OrderService orderService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalItemsSold", orderService.getTotalItemsSold());
        model.addAttribute("totalOrders", orderService.getAllOrders().size());
        model.addAttribute("totalRevenue", orderService.getTotalRevenue());
        model.addAttribute("recentOrders", orderService.getRecentOrders(10));
        return "admin/dashboard"; // templates/admin/dashboard.html
    }

    @GetMapping("/products")
    public String products(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Product> productPage = productRepository.findAll(
                PageRequest.of(page, ADMIN_PAGE_SIZE, Sort.by("id").ascending()));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "admin/products"; // templates/admin/products.html
    }

    @GetMapping("/products/new")
    public String newProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/product-form"; // templates/admin/product-form.html
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable long id, Model model, RedirectAttributes redirectAttributes) {
        return productRepository.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    return "admin/product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("toast", "Product not found.");
                    return "redirect:/admin/products";
                });
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product, RedirectAttributes redirectAttributes) {
        boolean isUpdate = product.getId() != null;
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("toast",
                isUpdate ? "Product updated successfully." : "Product created successfully.");
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable long id, RedirectAttributes redirectAttributes) {
        if (!productRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("toast", "Product not found.");
            return "redirect:/admin/products";
        }
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("toast", "Product deleted successfully.");
        return "redirect:/admin/products";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAllByOrderByCreatedAtDesc());
        return "admin/orders";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("toast", "Order status updated.");
        return "redirect:/admin/orders";
    }
}

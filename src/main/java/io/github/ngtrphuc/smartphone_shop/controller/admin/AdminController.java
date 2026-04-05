package io.github.ngtrphuc.smartphone_shop.controller.admin;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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

    private static final int ADMIN_PAGE_SIZE = 10;
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
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "all") String stock,
            @RequestParam(defaultValue = "id_asc") String sort,
            Model model) {
        populateProductListModel(model, page, keyword, brand, stock, sort);
        return "admin/products";
    }

    @GetMapping("/products/new")
    public String newProductForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "all") String stock,
            @RequestParam(defaultValue = "id_asc") String sort,
            Model model) {
        model.addAttribute("product", new Product());
        addProductListState(model, page, keyword, brand, stock, sort);
        return "admin/product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductForm(@PathVariable long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "all") String stock,
            @RequestParam(defaultValue = "id_asc") String sort,
            Model model,
            RedirectAttributes redirectAttributes) {
        return productRepository.findById(id)
                .map(product -> {
                    model.addAttribute("product", product);
                    addProductListState(model, page, keyword, brand, stock, sort);
                    return "admin/product-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("toast", "Product not found.");
                    addProductListState(redirectAttributes, page, keyword, brand, stock, sort);
                    return "redirect:/admin/products";
                });
    }

    @PostMapping("/products/save")
    public String saveProduct(@ModelAttribute Product product,
            @RequestParam(name = "returnPage", defaultValue = "0") int page,
            @RequestParam(name = "returnKeyword", required = false) String keyword,
            @RequestParam(name = "returnBrand", required = false) String brand,
            @RequestParam(name = "returnStock", defaultValue = "all") String stock,
            @RequestParam(name = "returnSort", defaultValue = "id_asc") String sort,
            RedirectAttributes redirectAttributes) {
        boolean isUpdate = product.getId() != null;
        productRepository.save(product);
        redirectAttributes.addFlashAttribute("toast",
                isUpdate ? "Product updated successfully." : "Product created successfully.");
        addProductListState(redirectAttributes, page, keyword, brand, stock, sort);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String brand,
            @RequestParam(defaultValue = "all") String stock,
            @RequestParam(defaultValue = "id_asc") String sort,
            RedirectAttributes redirectAttributes) {
        if (!productRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("toast", "Product not found.");
            addProductListState(redirectAttributes, page, keyword, brand, stock, sort);
            return "redirect:/admin/products";
        }
        productRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("toast", "Product deleted successfully.");
        addProductListState(redirectAttributes, page, keyword, brand, stock, sort);
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

    private void populateProductListModel(Model model, int page, String keyword, String brand, String stock, String sort) {
        String normalizedKeyword = normalizeText(keyword);
        String normalizedBrand = normalizeText(brand);
        String normalizedStock = normalizeStock(stock);
        String normalizedSort = normalizeSort(sort);

        List<Product> allProducts = productRepository.findAll();
        List<String> brands = allProducts.stream()
                .map(this::extractBrand)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<Product> filteredProducts = applyAdminFilters(allProducts, normalizedKeyword, normalizedBrand, normalizedStock);
        List<Product> sortedProducts = applyAdminSort(filteredProducts, normalizedSort);

        int totalProducts = sortedProducts.size();
        int totalPages = totalProducts == 0 ? 0 : (int) Math.ceil((double) totalProducts / ADMIN_PAGE_SIZE);
        int safePage = totalPages == 0 ? 0 : Math.max(0, Math.min(page, totalPages - 1));
        int fromIndex = Math.min(safePage * ADMIN_PAGE_SIZE, totalProducts);
        int toIndex = Math.min(fromIndex + ADMIN_PAGE_SIZE, totalProducts);

        model.addAttribute("products", sortedProducts.subList(fromIndex, toIndex));
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("brands", brands);
        addProductListState(model, safePage, normalizedKeyword, normalizedBrand, normalizedStock, normalizedSort);
    }

    private List<Product> applyAdminFilters(List<Product> products, String keyword, String brand, String stock) {
        return products.stream()
                .filter(product -> matchesKeyword(product, keyword))
                .filter(product -> brand == null || extractBrand(product).equalsIgnoreCase(brand))
                .filter(product -> matchesStock(product, stock))
                .toList();
    }

    private List<Product> applyAdminSort(List<Product> products, String sort) {
        Comparator<Product> comparator = switch (sort) {
            case "id_desc" -> Comparator.comparing((Product p) -> p.getId() == null ? Long.MIN_VALUE : p.getId()).reversed();
            case "name_asc" -> Comparator.comparing(p -> normalizeText(p.getName()) == null ? "" : normalizeText(p.getName()));
            case "name_desc" -> Comparator.comparing((Product p) -> normalizeText(p.getName()) == null ? "" : normalizeText(p.getName())).reversed();
            case "price_asc" -> Comparator.comparing(p -> p.getPrice() == null ? Double.MAX_VALUE : p.getPrice());
            case "price_desc" -> Comparator.comparing((Product p) -> p.getPrice() == null ? Double.MIN_VALUE : p.getPrice()).reversed();
            case "stock_asc" -> Comparator.comparing(p -> p.getStock() == null ? Integer.MAX_VALUE : p.getStock());
            case "stock_desc" -> Comparator.comparing((Product p) -> p.getStock() == null ? Integer.MIN_VALUE : p.getStock()).reversed();
            default -> Comparator.comparing(p -> p.getId() == null ? Long.MAX_VALUE : p.getId());
        };
        return products.stream().sorted(comparator).toList();
    }

    private boolean matchesKeyword(Product product, String keyword) {
        if (keyword == null) {
            return true;
        }
        return containsIgnoreCase(product.getName(), keyword)
                || containsIgnoreCase(product.getDescription(), keyword)
                || containsIgnoreCase(product.getOs(), keyword)
                || containsIgnoreCase(product.getChipset(), keyword)
                || containsIgnoreCase(product.getStorage(), keyword)
                || (product.getId() != null && String.valueOf(product.getId()).contains(keyword));
    }

    private boolean matchesStock(Product product, String stock) {
        int stockValue = product.getStock() == null ? 0 : product.getStock();
        return switch (stock) {
            case "in_stock" -> stockValue > 0;
            case "low_stock" -> stockValue > 0 && stockValue <= 5;
            case "out_of_stock" -> stockValue <= 0;
            default -> true;
        };
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }

    private String extractBrand(Product product) {
        String name = normalizeText(product.getName());
        if (name == null) {
            return "Other";
        }
        String firstToken = name.split("\\s+")[0];
        return switch (firstToken.toLowerCase(Locale.ROOT)) {
            case "iphone" -> "Apple";
            case "samsung" -> "Samsung";
            case "xiaomi" -> "Xiaomi";
            case "oppo" -> "OPPO";
            case "sony" -> "Sony";
            case "huawei" -> "Huawei";
            case "google" -> "Google";
            default -> capitalize(firstToken);
        };
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "Other";
        }
        String lower = value.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String normalizeStock(String stock) {
        return switch (stock) {
            case "in_stock", "low_stock", "out_of_stock" -> stock;
            default -> "all";
        };
    }

    private String normalizeSort(String sort) {
        return switch (sort) {
            case "id_desc", "name_asc", "name_desc", "price_asc", "price_desc", "stock_asc", "stock_desc" -> sort;
            default -> "id_asc";
        };
    }

    private void addProductListState(Model model, int page, String keyword, String brand, String stock, String sort) {
        model.addAttribute("currentPage", Math.max(page, 0));
        model.addAttribute("keyword", normalizeText(keyword));
        model.addAttribute("brand", normalizeText(brand));
        model.addAttribute("stock", normalizeStock(stock));
        model.addAttribute("sort", normalizeSort(sort));
    }

    private void addProductListState(RedirectAttributes redirectAttributes, int page, String keyword, String brand, String stock, String sort) {
        redirectAttributes.addAttribute("page", Math.max(page, 0));
        String normalizedKeyword = normalizeText(keyword);
        String normalizedBrand = normalizeText(brand);
        String normalizedStock = normalizeStock(stock);
        String normalizedSort = normalizeSort(sort);

        if (normalizedKeyword != null) {
            redirectAttributes.addAttribute("keyword", normalizedKeyword);
        }
        if (normalizedBrand != null) {
            redirectAttributes.addAttribute("brand", normalizedBrand);
        }
        if (!"all".equals(normalizedStock)) {
            redirectAttributes.addAttribute("stock", normalizedStock);
        }
        if (!"id_asc".equals(normalizedSort)) {
            redirectAttributes.addAttribute("sort", normalizedSort);
        }
    }
}

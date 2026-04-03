package io.github.ngtrphuc.smartphone_shop.controller;

import java.util.Comparator;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@Controller
public class MainController {

    private static final int PAGE_SIZE = 9;
    private final ProductRepository productRepository;

    public MainController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String batteryRange,
            @RequestParam(required = false) Integer batteryMin,
            @RequestParam(required = false) Integer batteryMax,
            @RequestParam(required = false) String screenSize,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Double resolvedPriceMin = priceMin;
        Double resolvedPriceMax = priceMax;
        if (priceRange != null) {
            switch (priceRange) {
                case "under150"  -> resolvedPriceMax = (resolvedPriceMax == null) ? 149999.0 : resolvedPriceMax;
                case "150to200"  -> { resolvedPriceMin = resolveMin(resolvedPriceMin, 150000.0);
                                      resolvedPriceMax = resolveMax(resolvedPriceMax, 199999.0); }
                case "200to250"  -> { resolvedPriceMin = resolveMin(resolvedPriceMin, 200000.0);
                                      resolvedPriceMax = resolveMax(resolvedPriceMax, 250000.0); }
                case "over250"   -> resolvedPriceMin = resolveMin(resolvedPriceMin, 250001.0);
            }
        }

        List<Product> all = productRepository.findAllWithFilters(
                blankToNull(keyword), resolvedPriceMin, resolvedPriceMax);

        all = applySortInMemory(all, sort);
        all = applyStringFilters(all, batteryRange, batteryMin, batteryMax, screenSize);

        int totalElements = all.size();
        int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / PAGE_SIZE);
        int safePage = Math.max(0, Math.min(page, totalPages - 1));

        List<Product> products = all.stream()
                .skip((long) safePage * PAGE_SIZE)
                .limit(PAGE_SIZE)
                .toList();

        model.addAttribute("products", products);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", (long) totalElements);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("batteryRange", batteryRange);
        model.addAttribute("batteryMin", batteryMin);
        model.addAttribute("batteryMax", batteryMax);
        model.addAttribute("screenSize", screenSize);
        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        if (id == null) return "redirect:/";
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/";
        model.addAttribute("product", product);
        return "detail";
    }

    private List<Product> applySortInMemory(List<Product> products, String sort) {
        if (sort == null) return products;
        return switch (sort) {
            case "name_asc" -> products.stream()
                    .sorted((a, b) -> {
                        String na = a.getName() != null ? a.getName() : "";
                        String nb = b.getName() != null ? b.getName() : "";
                        return na.compareToIgnoreCase(nb);
                    }).toList();
            case "name_desc" -> products.stream()
                    .sorted((a, b) -> {
                        String na = a.getName() != null ? a.getName() : "";
                        String nb = b.getName() != null ? b.getName() : "";
                        return nb.compareToIgnoreCase(na);
                    }).toList();
            case "price_asc" -> products.stream()
                    .sorted(Comparator.comparingDouble(
                            p -> p.getPrice() != null ? p.getPrice() : 0.0))
                    .toList();
            case "price_desc" -> products.stream()
                    .sorted(Comparator.comparingDouble(
                            (Product p) -> p.getPrice() != null ? p.getPrice() : 0.0).reversed())
                    .toList();
            default -> products;
        };
    }

    private List<Product> applyStringFilters(List<Product> products,
            String batteryRange, Integer batteryMin, Integer batteryMax, String screenSize) {
        if (batteryRange != null && !batteryRange.isBlank()) {
            products = switch (batteryRange) {
                case "under5000" -> products.stream()
                        .filter(p -> parseBattery(p.getBattery()) < 5000).toList();
                case "over5000" -> products.stream()
                        .filter(p -> parseBattery(p.getBattery()) >= 5000).toList();
                default -> products;
            };
        }
        if (batteryMin != null) {
            products = products.stream()
                    .filter(p -> parseBattery(p.getBattery()) >= batteryMin).toList();
        }
        if (batteryMax != null) {
            products = products.stream()
                    .filter(p -> parseBattery(p.getBattery()) <= batteryMax).toList();
        }
        if (screenSize != null && !screenSize.isBlank()) {
            products = switch (screenSize) {
                case "under6.5" -> products.stream()
                        .filter(p -> parseScreen(p.getSize()) < 6.5).toList();
                case "6.5to6.8" -> products.stream()
                        .filter(p -> { double s = parseScreen(p.getSize()); return s >= 6.5 && s <= 6.8; }).toList();
                case "over6.8" -> products.stream()
                        .filter(p -> parseScreen(p.getSize()) > 6.8).toList();
                default -> products;
            };
        }
        return products;
    }

    private Double resolveMin(Double existing, Double fallback) { return existing != null ? existing : fallback; }
    private Double resolveMax(Double existing, Double fallback) { return existing != null ? existing : fallback; }
    private String blankToNull(String s) { return (s == null || s.isBlank()) ? null : s; }

    private int parseBattery(String battery) {
        if (battery == null) return 0;
        try { return Integer.parseInt(battery.replaceAll("[^0-9]", "")); }
        catch (NumberFormatException | PatternSyntaxException e) { return 0; }
    }

    private double parseScreen(String size) {
        if (size == null) return 0;
        try { return Double.parseDouble(size.replaceAll("[^0-9.]", "")); }
        catch (NumberFormatException | PatternSyntaxException e) { return 0; }
    }
}
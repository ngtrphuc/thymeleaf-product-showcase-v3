package io.github.ngtrphuc.smartphone_shop.controller;

import java.util.List;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@Controller
public class MainController {

    private static final int PAGE_SIZE = 8;
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

        // Resolve price bounds từ priceRange shortcut
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

        Pageable pageable = buildPageable(sort, page);
        Page<Product> productPage = productRepository.findWithFilters(
                blankToNull(keyword), resolvedPriceMin, resolvedPriceMax, pageable);

        // Battery & screen size là String field → vẫn cần in-memory filter
        // Trade-off chấp nhận được vì page size nhỏ (8 records)
        List<Product> products = applyStringFilters(
                productPage.getContent(), batteryRange, batteryMin, batteryMax, screenSize);

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("batteryRange", batteryRange);
        model.addAttribute("batteryMin", batteryMin);
        model.addAttribute("batteryMax", batteryMax);
        model.addAttribute("screenSize", screenSize);
        addCommonAttributes(model);
        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        if (id == null) return "redirect:/";
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/";
        model.addAttribute("product", product);
        addCommonAttributes(model);
        return "detail";
    }

    // ===== PRIVATE HELPERS =====

    private Pageable buildPageable(String sort, int page) {
        Sort s = switch (sort != null ? sort : "") {
            case "name_asc"   -> Sort.by("name").ascending();
            case "name_desc"  -> Sort.by("name").descending();
            case "price_asc"  -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            default           -> Sort.by("id").ascending();
        };
        return PageRequest.of(Math.max(page, 0), PAGE_SIZE, s);
    }

    private List<Product> applyStringFilters(List<Product> products,
            String batteryRange, Integer batteryMin, Integer batteryMax, String screenSize) {

        if (batteryRange != null && !batteryRange.isBlank()) {
            products = switch (batteryRange) {
                case "under5000" -> products.stream().filter(p -> parseBattery(p.getBattery()) < 5000).collect(Collectors.toList());
                case "over5000"  -> products.stream().filter(p -> parseBattery(p.getBattery()) >= 5000).collect(Collectors.toList());
                default -> products;
            };
        }
        if (batteryMin != null) products = products.stream().filter(p -> parseBattery(p.getBattery()) >= batteryMin).collect(Collectors.toList());
        if (batteryMax != null) products = products.stream().filter(p -> parseBattery(p.getBattery()) <= batteryMax).collect(Collectors.toList());

        if (screenSize != null && !screenSize.isBlank()) {
            products = switch (screenSize) {
                case "under6.5" -> products.stream().filter(p -> parseScreen(p.getSize()) < 6.5).collect(Collectors.toList());
                case "6.5to6.8" -> products.stream().filter(p -> { double s = parseScreen(p.getSize()); return s >= 6.5 && s <= 6.8; }).collect(Collectors.toList());
                case "over6.8"  -> products.stream().filter(p -> parseScreen(p.getSize()) > 6.8).collect(Collectors.toList());
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

    private void addCommonAttributes(Model model) {
        model.addAttribute("shopname", "Smartphone Shop");
        model.addAttribute("address", "Asaka, Saitama, Japan");
    }
}
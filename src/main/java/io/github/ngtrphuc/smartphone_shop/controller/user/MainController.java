package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;

@Controller
public class MainController {

    private static final int DESKTOP_PAGE_SIZE = 9;
    private static final int COMPACT_PAGE_SIZE = 8;
    private static final List<String> BRANDS = Arrays.asList(
            "Apple", "Samsung", "Google", "Oppo", "Vivo", "Xiaomi", "Sony", "ASUS", "ZTE"
    );

    private final ProductRepository productRepository;

    public MainController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String batteryRange,
            @RequestParam(required = false) Integer batteryMin,
            @RequestParam(required = false) Integer batteryMax,
            @RequestParam(required = false) String screenSize,
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Double resolvedPriceMin = priceMin;
        Double resolvedPriceMax = priceMax;
        if (priceRange != null) {
            switch (priceRange) {
                case "under150" -> resolvedPriceMax = (resolvedPriceMax == null) ? 149999.0 : resolvedPriceMax;
                case "150to200" -> {
                    resolvedPriceMin = resolveMin(resolvedPriceMin, 150000.0);
                    resolvedPriceMax = resolveMax(resolvedPriceMax, 199999.0);
                }
                case "200to250" -> {
                    resolvedPriceMin = resolveMin(resolvedPriceMin, 200000.0);
                    resolvedPriceMax = resolveMax(resolvedPriceMax, 250000.0);
                }
                case "over250" -> resolvedPriceMin = resolveMin(resolvedPriceMin, 250001.0);
            }
        }

        int effectivePageSize = resolvePageSize(pageSize);
        int safeRequestedPage = Math.max(page, 0);
        Sort requestedSort = Objects.requireNonNull(resolveSort(sort));
        List<Product> products;
        long totalElements;
        int totalPages;
        int safePage;

        if (requiresInMemoryFiltering(brand, batteryRange, batteryMin, batteryMax, screenSize)) {
            List<Product> all = productRepository.findAllWithFilters(
                    blankToNull(keyword), resolvedPriceMin, resolvedPriceMax);

            if (brand != null && !brand.isBlank()) {
                final String normalizedBrand = brand.toLowerCase(Locale.ROOT);
                all = all.stream()
                        .filter(p -> inferBrand(p).equals(normalizedBrand))
                        .toList();
            }

            all = applySortInMemory(all, sort);
            all = applyStringFilters(all, batteryRange, batteryMin, batteryMax, screenSize);

            totalElements = all.size();
            totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / effectivePageSize);
            safePage = Math.max(0, Math.min(safeRequestedPage, totalPages - 1));
            products = all.stream()
                    .skip((long) safePage * effectivePageSize)
                    .limit(effectivePageSize)
                    .toList();
        } else {
            Page<Product> productPage = productRepository.findWithFilters(
                    blankToNull(keyword),
                    resolvedPriceMin,
                    resolvedPriceMax,
                    PageRequest.of(safeRequestedPage, effectivePageSize, requestedSort));
            if (productPage.isEmpty() && safeRequestedPage > 0 && productPage.getTotalPages() > 0) {
                productPage = productRepository.findWithFilters(
                        blankToNull(keyword),
                        resolvedPriceMin,
                        resolvedPriceMax,
                        PageRequest.of(productPage.getTotalPages() - 1, effectivePageSize, requestedSort));
            }
            products = productPage.getContent();
            totalElements = productPage.getTotalElements();
            totalPages = Math.max(productPage.getTotalPages(), 1);
            safePage = Math.max(0, Math.min(productPage.getNumber(), totalPages - 1));
        }

        model.addAttribute("products", products);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", (long) totalElements);
        model.addAttribute("pageSize", effectivePageSize);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", sort);
        model.addAttribute("brand", brand);
        model.addAttribute("brands", BRANDS);
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
    public String productDetail(@PathVariable long id,
            @RequestParam MultiValueMap<String, String> requestParams,
            Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/";
        model.addAttribute("product", product);
        model.addAttribute("backUrl", buildBackUrl(requestParams));
        return "detail";
    }

    private List<Product> applySortInMemory(List<Product> products, String sort) {
        if (sort == null) return products;
        return switch (sort) {
            case "name_asc" -> products.stream()
                    .sorted(Comparator.comparing(p -> normalizeNameForSort(p.getName())))
                    .toList();
            case "name_desc" -> products.stream()
                    .sorted(Comparator.comparing((Product p) -> normalizeNameForSort(p.getName())).reversed())
                    .toList();
            case "price_asc" -> products.stream()
                    .sorted(Comparator.comparingDouble(this::safePrice))
                    .toList();
            case "price_desc" -> products.stream()
                    .sorted(Comparator.comparingDouble(this::safePrice).reversed())
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
                        .filter(p -> {
                            double s = parseScreen(p.getSize());
                            return s >= 6.5 && s <= 6.8;
                        }).toList();
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
    private String normalizeNameForSort(String name) { return name == null ? "" : name.trim().toLowerCase(Locale.ROOT); }
    private double safePrice(Product product) { return Objects.requireNonNullElse(product.getPrice(), 0.0); }

    private boolean requiresInMemoryFiltering(String brand,
            String batteryRange, Integer batteryMin, Integer batteryMax, String screenSize) {
        return (brand != null && !brand.isBlank())
                || (batteryRange != null && !batteryRange.isBlank())
                || batteryMin != null
                || batteryMax != null
                || (screenSize != null && !screenSize.isBlank());
    }

    private Sort resolveSort(String sort) {
        if (sort == null) {
            return Sort.by("id").ascending();
        }
        return switch (sort) {
            case "name_asc" -> Sort.by(Sort.Order.asc("name").ignoreCase());
            case "name_desc" -> Sort.by(Sort.Order.desc("name").ignoreCase());
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            default -> Sort.by("id").ascending();
        };
    }

    private String inferBrand(Product product) {
        String name = product != null ? product.getName() : null;
        if (name == null || name.isBlank()) {
            return "";
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("iphone")) return "apple";
        if (normalized.startsWith("galaxy")) return "samsung";
        if (normalized.startsWith("pixel")) return "google";
        if (normalized.startsWith("oppo") || normalized.startsWith("find")) return "oppo";
        if (normalized.startsWith("vivo")) return "vivo";
        if (normalized.startsWith("xiaomi")) return "xiaomi";
        if (normalized.startsWith("xperia") || normalized.startsWith("sony")) return "sony";
        if (normalized.startsWith("rog") || normalized.startsWith("asus")) return "asus";
        if (normalized.startsWith("redmagic") || normalized.startsWith("zte") || normalized.startsWith("nubia")) return "zte";
        return normalized.split("\\s+")[0];
    }

    private String buildBackUrl(MultiValueMap<String, String> requestParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/");
        requestParams.forEach((key, values) -> {
            if (key == null || key.isBlank() || values == null) {
                return;
            }
            for (String value : values) {
                if (value != null && !value.isBlank()) {
                    builder.queryParam(key, value);
                }
            }
        });
        return builder.build().encode().toUriString();
    }

    private int resolvePageSize(Integer pageSize) {
        return pageSize != null && pageSize == COMPACT_PAGE_SIZE ? COMPACT_PAGE_SIZE : DESKTOP_PAGE_SIZE;
    }

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

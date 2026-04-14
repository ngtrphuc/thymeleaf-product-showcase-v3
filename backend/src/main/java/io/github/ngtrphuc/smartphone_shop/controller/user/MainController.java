package io.github.ngtrphuc.smartphone_shop.controller.user;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import io.github.ngtrphuc.smartphone_shop.support.StorefrontSupport;
import io.github.ngtrphuc.smartphone_shop.service.WishlistService;

@Controller
public class MainController {

    private static final int DESKTOP_PAGE_SIZE = 9;
    private static final int COMPACT_PAGE_SIZE = 8;

    private final ProductRepository productRepository;
    private final WishlistService wishlistService;

    public MainController(ProductRepository productRepository, WishlistService wishlistService) {
        this.productRepository = productRepository;
        this.wishlistService = wishlistService;
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
            Authentication authentication,
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
        String normalizedSort = normalizeSort(sort);
        Sort requestedSort = Objects.requireNonNull(resolveSort(normalizedSort));
        List<Product> products;
        long totalElements;
        int totalPages;
        int safePage;
        int activeFilterCount = countActiveFilters(
                keyword, brand, priceRange, priceMin, priceMax, batteryRange, batteryMin, batteryMax, screenSize);

        if (requiresInMemoryFiltering(brand, batteryRange, batteryMin, batteryMax, screenSize)) {
            List<Product> all = productRepository.findAllWithFilters(
                    blankToNull(keyword), resolvedPriceMin, resolvedPriceMax);

            if (brand != null && !brand.isBlank()) {
                all = all.stream()
                        .filter(p -> StorefrontSupport.extractBrand(p != null ? p.getName() : null)
                                .equalsIgnoreCase(brand))
                        .toList();
            }

            all = applySortInMemory(all, normalizedSort);
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

        List<String> brands = resolveAvailableBrands();
        model.addAttribute("products", products);
        model.addAttribute("currentPage", safePage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalElements", (long) totalElements);
        model.addAttribute("pageSize", effectivePageSize);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sort", normalizedSort);
        model.addAttribute("brand", brand);
        model.addAttribute("brands", brands);
        model.addAttribute("brandCount", brands.size());
        model.addAttribute("priceRange", priceRange);
        model.addAttribute("priceMin", priceMin);
        model.addAttribute("priceMax", priceMax);
        model.addAttribute("batteryRange", batteryRange);
        model.addAttribute("batteryMin", batteryMin);
        model.addAttribute("batteryMax", batteryMax);
        model.addAttribute("screenSize", screenSize);
        model.addAttribute("activeFilterCount", activeFilterCount);
        model.addAttribute("hasActiveFilters", activeFilterCount > 0);
        model.addAttribute("wishlistedProductIds", resolveWishlistedProductIds(authentication));
        return "index";
    }

    @GetMapping("/product/{id}")
    public String productDetail(@PathVariable long id,
            @RequestParam MultiValueMap<String, String> requestParams,
            Authentication authentication,
            Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) return "redirect:/";
        model.addAttribute("product", product);
        model.addAttribute("productBrand", StorefrontSupport.extractBrand(product.getName()));
        model.addAttribute("recommendedProducts", resolveRecommendedProducts(product));
        model.addAttribute("backUrl", buildBackUrl(requestParams));
        model.addAttribute("wishlisted", isAuthenticatedUser(authentication)
                && wishlistService.isWishlisted(authentication.getName(), id));
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
            return Sort.by(Sort.Order.asc("name").ignoreCase());
        }
        return switch (sort) {
            case "name_asc" -> Sort.by(Sort.Order.asc("name").ignoreCase());
            case "name_desc" -> Sort.by(Sort.Order.desc("name").ignoreCase());
            case "price_asc" -> Sort.by("price").ascending();
            case "price_desc" -> Sort.by("price").descending();
            default -> Sort.by(Sort.Order.asc("name").ignoreCase());
        };
    }

    private String normalizeSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return "name_asc";
        }
        return switch (sort) {
            case "name_desc", "price_asc", "price_desc" -> sort;
            default -> "name_asc";
        };
    }

    private List<String> resolveAvailableBrands() {
        return productRepository.findAllNamesOrdered().stream()
                .map(StorefrontSupport::extractBrand)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private int countActiveFilters(String keyword, String brand, String priceRange,
            Double priceMin, Double priceMax, String batteryRange, Integer batteryMin,
            Integer batteryMax, String screenSize) {
        int count = 0;
        if (keyword != null && !keyword.isBlank()) count++;
        if (brand != null && !brand.isBlank()) count++;
        if (priceRange != null && !priceRange.isBlank()) count++;
        if (priceMin != null || priceMax != null) count++;
        if (batteryRange != null && !batteryRange.isBlank()) count++;
        if (batteryMin != null || batteryMax != null) count++;
        if (screenSize != null && !screenSize.isBlank()) count++;
        return count;
    }

    private List<Product> resolveRecommendedProducts(Product currentProduct) {
        if (currentProduct == null || currentProduct.getId() == null) {
            return List.of();
        }
        double targetPrice = Objects.requireNonNullElse(currentProduct.getPrice(), 0.0);
        return productRepository.findRecommendedProducts(
                currentProduct.getId(),
                targetPrice,
                PageRequest.of(0, 4));
    }

    private String buildBackUrl(MultiValueMap<String, String> requestParams) {
        String from = firstNonBlank(requestParams.get("from"));
        if ("wishlist".equalsIgnoreCase(from)) {
            return "/wishlist";
        }
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

    private String firstNonBlank(List<String> values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
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

    private boolean isAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private java.util.Set<Long> resolveWishlistedProductIds(Authentication authentication) {
        if (!isAuthenticatedUser(authentication)) {
            return java.util.Set.of();
        }
        return wishlistService.getWishlistedProductIds(authentication.getName());
    }
}

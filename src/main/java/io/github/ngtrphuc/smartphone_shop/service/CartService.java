package io.github.ngtrphuc.smartphone_shop.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.github.ngtrphuc.smartphone_shop.model.CartItem;
import io.github.ngtrphuc.smartphone_shop.model.CartItemEntity;
import io.github.ngtrphuc.smartphone_shop.model.Product;
import io.github.ngtrphuc.smartphone_shop.repository.CartItemRepository;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @SuppressWarnings("unchecked")
    public List<CartItem> getSessionCart(HttpSession session) {
        Object obj = session.getAttribute("cart");
        if (obj == null) {
            List<CartItem> cart = new ArrayList<>();
            session.setAttribute("cart", cart);
            return cart;
        }
        return (List<CartItem>) obj;
    }

    public void syncCartCount(HttpSession session, String email) {
        int count;
        if (isLoggedIn(email)) {
            count = getDbCart(email)
                    .stream().mapToInt(CartItem::getQuantity).sum();
        } else {
            count = getSessionCart(session)
                    .stream().mapToInt(CartItem::getQuantity).sum();
        }
        session.setAttribute("cartCount", count);
    }

    @Transactional
    public void mergeSessionCartToDb(HttpSession session, String email) {
        List<CartItem> sessionCart = getSessionCart(session);
        if (sessionCart.isEmpty()) {
            return;
        }

        List<Long> productIds = sessionCart.stream()
                .map(CartItem::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (productIds.isEmpty()) {
            session.removeAttribute("cart");
            syncCartCount(session, email);
            return;
        }

        Map<Long, Product> productMap = productRepository.findAllByIdIn(productIds)
                .stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Product::getId, p -> p));

        Map<Long, CartItemEntity> existingByProductId = new LinkedHashMap<>();
        for (CartItemEntity existing : cartItemRepository.findByUserEmail(email)) {
            if (existing.getProductId() != null) {
                existingByProductId.putIfAbsent(existing.getProductId(), existing);
            }
        }

        for (CartItem item : sessionCart) {
            Long itemId = item.getId();
            if (itemId == null) {
                continue;
            }
            Product product = productMap.get(itemId);
            int maxStock = product != null && product.getStock() != null ? product.getStock() : 0;
            if (maxStock <= 0) {
                continue;
            }

            int requestedQty = Math.max(item.getQuantity(), 0);
            if (requestedQty == 0) {
                continue;
            }

            CartItemEntity existing = existingByProductId.get(itemId);
            if (existing != null) {
                int mergedQty = Math.min(existing.getQuantity() + requestedQty, maxStock);
                if (mergedQty != existing.getQuantity()) {
                    existing.setQuantity(mergedQty);
                    cartItemRepository.save(existing);
                }
            } else {
                int initialQty = Math.min(requestedQty, maxStock);
                if (initialQty > 0) {
                    CartItemEntity created = new CartItemEntity(email, itemId, initialQty);
                    cartItemRepository.save(created);
                    existingByProductId.put(itemId, created);
                }
            }
        }
        session.removeAttribute("cart");
        syncCartCount(session, email);
    }

    @Transactional
    public List<CartItem> getDbCart(String email) {
        List<CartItemEntity> entities = cartItemRepository.findByUserEmail(email);
        if (entities.isEmpty()) {
            return List.of();
        }

        List<Long> productIds = entities.stream().map(CartItemEntity::getProductId).toList();
        Map<Long, Product> productMap = productRepository.findAllByIdIn(productIds)
                .stream()
                .filter(p -> p.getId() != null)
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<CartItemEntity> orphanedItems = entities.stream()
                .filter(entity -> !productMap.containsKey(entity.getProductId()))
                .toList();
        if (!orphanedItems.isEmpty()) {
            cartItemRepository.deleteAll(orphanedItems);
        }

        List<CartItem> result = entities.stream()
                .map(e -> {
                    Product p = productMap.get(e.getProductId());
                    if (p == null) {
                        return null;
                    }
                    return new CartItem(e.getProductId(), p.getName(), p.getPrice(), e.getQuantity());
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return result;
    }

    @Transactional
    public void addItem(String email, HttpSession session, long productId) {
        Product p = productRepository.findById(productId).orElse(null);
        if (p == null) {
            return;
        }
        int maxStock = (p.getStock() != null) ? p.getStock() : 0;
        if (maxStock <= 0) {
            return;
        }

        if (isLoggedIn(email)) {
            Optional<CartItemEntity> existing
                    = cartItemRepository.findByUserEmailAndProductId(email, productId);
            if (existing.isPresent()) {
                CartItemEntity e = existing.get();
                if (e.getQuantity() < maxStock) {
                    e.setQuantity(e.getQuantity() + 1);
                    cartItemRepository.save(e);
                }
            } else {
                cartItemRepository.save(new CartItemEntity(email, productId, 1));
            }
        } else {
            List<CartItem> cart = getSessionCart(session);
            CartItem found = cart.stream()
                    .filter(i -> i.getId() != null && i.getId() == productId)
                    .findFirst().orElse(null);
            if (found != null) {
                if (found.getQuantity() < maxStock) {
                    found.setQuantity(found.getQuantity() + 1);
                }
            } else {
                cart.add(new CartItem(productId, p.getName(), p.getPrice(), 1));
            }
        }
    }

    @Transactional
    public void increaseItem(String email, HttpSession session, long productId) {
        Product p = productRepository.findById(productId).orElse(null);
        int maxStock = (p != null && p.getStock() != null) ? p.getStock() : 0;
        if (maxStock <= 0) {
            return;
        }
        if (isLoggedIn(email)) {
            cartItemRepository.findByUserEmailAndProductId(email, productId).ifPresent(e -> {
                if (e.getQuantity() < maxStock) {
                    e.setQuantity(e.getQuantity() + 1);
                    cartItemRepository.save(e);
                }
            });
        } else {
            getSessionCart(session).stream()
                    .filter(i -> i.getId() != null && i.getId() == productId)
                    .findFirst()
                    .ifPresent(i -> {
                        if (i.getQuantity() < maxStock) {
                            i.setQuantity(i.getQuantity() + 1);
                        }
                    });
        }
    }

    @Transactional
    public void decreaseItem(String email, HttpSession session, long productId) {
        if (isLoggedIn(email)) {
            cartItemRepository.findByUserEmailAndProductId(email, productId).ifPresent(e -> {
                if (e.getQuantity() > 1) {
                    e.setQuantity(e.getQuantity() - 1);
                    cartItemRepository.save(e);
                } else {
                    cartItemRepository.delete(e);
                }
            });
        } else {
            List<CartItem> cart = getSessionCart(session);
            CartItem found = cart.stream()
                    .filter(i -> i.getId() != null && i.getId() == productId)
                    .findFirst().orElse(null);
            if (found != null) {
                if (found.getQuantity() > 1) {
                    found.setQuantity(found.getQuantity() - 1); 
                }else {
                    cart.remove(found);
                }
            }
        }
    }

    @Transactional
    public void removeItem(String email, HttpSession session, long productId) {
        if (isLoggedIn(email)) {
            cartItemRepository.deleteByUserEmailAndProductId(email, productId);
        } else {
            getSessionCart(session).removeIf(i -> i.getId() != null && i.getId() == productId);
        }
    }

    @Transactional
    public void clearCart(String email, HttpSession session) {
        if (isLoggedIn(email)) {
            cartItemRepository.deleteByUserEmail(email);
        } else {
            session.removeAttribute("cart");
        }
        session.setAttribute("cartCount", 0);
    }

    public List<CartItem> getCart(String email, HttpSession session) {
        return isLoggedIn(email) ? getDbCart(email) : getSessionCart(session);
    }

    public double calculateTotal(List<CartItem> cart) {
        return cart.stream()
                .mapToDouble(i -> Optional.ofNullable(i.getPrice()).orElse(0.0) * i.getQuantity())
                .sum();
    }

    private boolean isLoggedIn(String email) {
        return email != null && !email.equals("anonymousUser");
    }
}

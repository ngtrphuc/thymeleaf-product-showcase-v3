package io.github.ngtrphuc.smartphone_shop.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import io.github.ngtrphuc.smartphone_shop.model.CartItem;
import io.github.ngtrphuc.smartphone_shop.model.Order;
import io.github.ngtrphuc.smartphone_shop.model.OrderItem;
import io.github.ngtrphuc.smartphone_shop.repository.OrderRepository;
import io.github.ngtrphuc.smartphone_shop.repository.ProductRepository;
import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order createOrder(String userEmail, String name, String phone,
            String address, List<CartItem> cartItems, double total) {
        Order order = new Order();
        order.setUserEmail(userEmail);
        order.setCustomerName(name);
        order.setPhoneNumber(phone);
        order.setShippingAddress(address);
        order.setTotalAmount(total);
        order.setStatus("pending");

        for (CartItem item : cartItems) {
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProductId(item.getId());
            oi.setProductName(item.getName());
            oi.setPrice(item.getPrice());
            oi.setQuantity(item.getQuantity());
            order.getItems().add(oi);

            Long productId = item.getId();
            if (productId != null) {
                productRepository.findById(productId).ifPresent(p -> {
                    int newStock = Math.max(0, p.getStock() - item.getQuantity());
                    p.setStock(newStock);
                    productRepository.save(p);
                });
            }
        }
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByUser(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Order> getRecentOrders(int limit) {
        return orderRepository.findRecentOrders(PageRequest.of(0, limit));
    }

    public double getTotalRevenue() {
        Double result = orderRepository.sumRevenueExcludingCancelled();
        return result != null ? result : 0.0;
    }

    public long getTotalItemsSold() {
        Long result = orderRepository.sumItemsSoldExcludingCancelled();
        return result != null ? result : 0L;
    }

    @Transactional
    public void updateStatus(Long orderId, String newStatus) {
        if (orderId == null) return;
        orderRepository.findById(orderId).ifPresent(o -> {
            String oldStatus = o.getStatus();

            if ("cancelled".equals(oldStatus) && !"cancelled".equals(newStatus)) {
                o.getItems().forEach(item ->
                    productRepository.findById(item.getProductId()).ifPresent(p -> {
                        int newStock = Math.max(0, p.getStock() - item.getQuantity());
                        p.setStock(newStock);
                        productRepository.save(p);
                    })
                );
            }

            if (!"cancelled".equals(oldStatus) && "cancelled".equals(newStatus)) {
                o.getItems().forEach(item ->
                    productRepository.findById(item.getProductId()).ifPresent(p -> {
                        p.setStock(p.getStock() + item.getQuantity());
                        productRepository.save(p);
                    })
                );
            }

            o.setStatus(newStatus);
            orderRepository.save(o);
        });
    }

    @Transactional
    public boolean cancelOrder(Long orderId, String userEmail) {
        return orderRepository.findById(orderId)
                .filter(o -> o.getUserEmail().equals(userEmail))
                .filter(o -> "pending".equals(o.getStatus()) || "processing".equals(o.getStatus()))
                .map(o -> {
                    o.getItems().forEach(item ->
                        productRepository.findById(item.getProductId()).ifPresent(p -> {
                            p.setStock(p.getStock() + item.getQuantity());
                            productRepository.save(p);
                        })
                    );
                    o.setStatus("cancelled");
                    orderRepository.save(o);
                    return true;
                })
                .orElse(false);
    }
}
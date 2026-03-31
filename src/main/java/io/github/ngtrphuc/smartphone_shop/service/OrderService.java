package io.github.ngtrphuc.smartphone_shop.service;

import java.util.List;

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

    public long getTotalItemsSold() {
        return orderRepository.findAll().stream()
                .flatMap(o -> o.getItems().stream())
                .mapToLong(item -> item.getQuantity())
                .sum();
    }

    public void updateStatus(Long orderId, String status) {
        if (orderId != null) {
            orderRepository.findById(orderId).ifPresent(o -> {
                o.setStatus(status);
                orderRepository.save(o);
            });
        }
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
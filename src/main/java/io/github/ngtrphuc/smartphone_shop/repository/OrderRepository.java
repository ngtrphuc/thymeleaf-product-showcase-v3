package io.github.ngtrphuc.smartphone_shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.ngtrphuc.smartphone_shop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserEmailOrderByCreatedAtDesc(String email);
    List<Order> findAllByOrderByCreatedAtDesc(); // dùng cho admin
}
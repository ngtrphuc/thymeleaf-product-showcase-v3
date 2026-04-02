package io.github.ngtrphuc.smartphone_shop.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.ngtrphuc.smartphone_shop.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.userEmail = :email ORDER BY o.createdAt DESC")
    List<Order> findByUserEmailOrderByCreatedAtDesc(@Param("email") String email);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.createdAt DESC")
    List<Order> findAllByOrderByCreatedAtDesc();

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(Pageable pageable);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'cancelled'")
    Double sumRevenueExcludingCancelled();

    @Query("SELECT COALESCE(SUM(oi.quantity), 0) FROM OrderItem oi WHERE oi.order.status != 'cancelled'")
    Long sumItemsSoldExcludingCancelled();
}

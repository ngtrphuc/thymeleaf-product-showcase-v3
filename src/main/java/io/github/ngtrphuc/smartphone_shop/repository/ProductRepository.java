package io.github.ngtrphuc.smartphone_shop.repository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import io.github.ngtrphuc.smartphone_shop.model.Product;
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:priceMin IS NULL OR p.price >= :priceMin)
          AND (:priceMax IS NULL OR p.price <= :priceMax)
        """)
    Page<Product> findWithFilters(
            @Param("keyword") String keyword,
            @Param("priceMin") Double priceMin,
            @Param("priceMax") Double priceMax,
            Pageable pageable);
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
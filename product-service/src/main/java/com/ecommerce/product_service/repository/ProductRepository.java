package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByActiveTrue();
    List<Product> findByCategoryIdAndActiveTrue(UUID categoryId);
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
}
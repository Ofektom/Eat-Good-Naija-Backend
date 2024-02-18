package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.enums.Category;
import com.example.eatgoodliveproject.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long userId);

    Optional<Product> findProductByNameIgnoreCase(String name);

    Page<Product> findAllBy(Pageable pageable);

    Page<Product> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Product> findAllByOrderByPriceAsc(Pageable pageable);

    Page<Product> findAllByCategory(Category category, Pageable pageable);

    Page<Product> findProductByNameLike(String keyword, Pageable pageable);

    Page<Product> findAllByOrderBySizeAsc(Pageable pageable);
    List<Product> findByCategory(Category category);

    Product findByUserIdAndId(Long userId, Long productId);
}

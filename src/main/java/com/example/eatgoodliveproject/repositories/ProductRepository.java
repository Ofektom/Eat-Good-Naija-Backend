package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}

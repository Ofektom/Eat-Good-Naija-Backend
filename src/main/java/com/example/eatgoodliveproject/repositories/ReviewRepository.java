package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.model.Orders;
import com.example.eatgoodliveproject.model.Product;
import com.example.eatgoodliveproject.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Optional<Review> findByIdAndOrder(Long reviewId, Orders order);
}

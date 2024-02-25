package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findAllByUserId(Long userId);

    Orders findByIdAndUserId(Long userId, Long orderId);

    Optional<Orders> findByOrderVerification(String orderId);
}

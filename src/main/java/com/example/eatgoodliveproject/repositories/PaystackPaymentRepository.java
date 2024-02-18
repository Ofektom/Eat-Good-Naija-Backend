package com.example.eatgoodliveproject.repositories;

import com.example.eatgoodliveproject.model.PaymentPaystack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaystackPaymentRepository extends JpaRepository<PaymentPaystack, Long> {
}
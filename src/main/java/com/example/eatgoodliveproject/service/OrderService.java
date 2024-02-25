package com.example.eatgoodliveproject.service;

import com.example.eatgoodliveproject.dto.*;
import com.example.eatgoodliveproject.enums.PaymentMethod;
import com.example.eatgoodliveproject.enums.TrackingStatus;
import com.example.eatgoodliveproject.model.Orders;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Long cartId, Long userId);
    OrderResponseDto verifyPaymentAndConfirmOrder(String paymentReference, Long orderId) throws RuntimeException;
    List<OrderDto> getAllOrders();
    List<OrderDto> getOrdersByUserId(Long userId);
    OrderDto getOrder(Long userId, Long orderId);
    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ResponseEntity<TrackingDto> alertCustomers(Long userId, Long orderId);

    ResponseEntity<?> acceptOrder(Long orderId);

    ResponseEntity<String> orderReady(Long orderId);
    ResponseEntity<String> orderDelivered(Long orderId);
}

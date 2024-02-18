package com.example.eatgoodliveproject.service;

import com.example.eatgoodliveproject.dto.OrderDto;
import com.example.eatgoodliveproject.dto.OrderResponse;
import com.example.eatgoodliveproject.dto.PaymentDto;
import com.example.eatgoodliveproject.dto.TrackingDto;
import com.example.eatgoodliveproject.enums.PaymentMethod;
import com.example.eatgoodliveproject.enums.TrackingStatus;
import com.example.eatgoodliveproject.model.Orders;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    OrderDto placeOrder(Long cartId, Long userId);

    List<OrderDto> getAllOrders();
    List<OrderDto> getOrdersByUserId(Long userId);
    OrderDto getOrder(Long userId, Long orderId);
    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ResponseEntity<TrackingDto> alertCustomers(Long userId, Long orderId);

    ResponseEntity<?> acceptOrder(Long orderId);

    ResponseEntity<String> orderReady(Long orderId);
    ResponseEntity<String> orderDelivered(Long orderId);
}

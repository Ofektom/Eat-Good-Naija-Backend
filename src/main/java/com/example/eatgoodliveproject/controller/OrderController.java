package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.config.AppConstants;
import com.example.eatgoodliveproject.dto.OrderDto;
import com.example.eatgoodliveproject.dto.OrderResponse;
import com.example.eatgoodliveproject.dto.PaymentDto;
import com.example.eatgoodliveproject.dto.TrackingDto;
import com.example.eatgoodliveproject.enums.PaymentMethod;
import com.example.eatgoodliveproject.enums.TrackingStatus;
import com.example.eatgoodliveproject.model.Orders;
import com.example.eatgoodliveproject.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Endpoint to place an order from cart items and make payment
    @PostMapping("/place-order/{cartId}/{userId}")
    public ResponseEntity<OrderDto> placeOrder(
            @PathVariable Long cartId,
            @PathVariable Long userId
            ) {
        return ResponseEntity.ok(orderService.placeOrder(cartId, userId));
    }

    @PostMapping (value = "/verifyPayment")
    public ResponseEntity<?> confirmOrder(@RequestParam String paymentReference, @RequestParam Long orderId) throws RuntimeException{
        return new ResponseEntity<>(orderService.verifyPaymentAndConfirmOrder(paymentReference, orderId), HttpStatus.OK);
    }


    // Endpoint to view all orders
    @GetMapping("/view-all-orders")
    public ResponseEntity<List<OrderDto>> viewAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }


    @PutMapping("/accept-order/{orderId}")
    public ResponseEntity<?> acceptOrder(@PathVariable Long orderId){
        return orderService.acceptOrder(orderId);
    }

    @PutMapping("/order-ready/{orderId}")
    public ResponseEntity<?> orderReady(@PathVariable Long orderId){
        return orderService.orderReady(orderId);
    }

    @PutMapping("/order-delivered/{orderId}")
    public ResponseEntity<?> orderDelivered(@PathVariable Long orderId){
        return orderService.orderDelivered(orderId);
    }

    // Endpoint to alert the customer about order status
    @GetMapping("/alert-customer")
    public ResponseEntity<TrackingDto> alertCustomer(
            @RequestParam Long userId,
            @RequestParam Long orderId
    ) {
        return orderService.alertCustomers(userId, orderId);
    }


    @GetMapping("/all/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ORDERS_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {

        OrderResponse orderResponse = orderService.getAllOrders(pageNumber, pageSize, sortBy, sortOrder);

        return new ResponseEntity<>(orderResponse, HttpStatus.FOUND);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersByUser(@PathVariable Long userId) {
        List<OrderDto> orders = orderService.getOrdersByUserId(userId);

        return new ResponseEntity<>(orders, HttpStatus.FOUND);
    }

    @GetMapping("/users/{userId}/order/{orderId}")
    public ResponseEntity<OrderDto> getOrderByUser(@PathVariable Long userId, @PathVariable Long orderId) {
        OrderDto order = orderService.getOrder(userId, orderId);
        return new ResponseEntity<>(order, HttpStatus.FOUND);
    }
}

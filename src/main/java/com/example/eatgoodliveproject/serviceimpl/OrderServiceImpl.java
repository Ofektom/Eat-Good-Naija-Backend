package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.dto.*;
import com.example.eatgoodliveproject.enums.OrderStatus;
import com.example.eatgoodliveproject.enums.ShippingMethod;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
import com.example.eatgoodliveproject.model.*;
import com.example.eatgoodliveproject.model.PaymentPaystack;
import com.example.eatgoodliveproject.repositories.*;
import com.example.eatgoodliveproject.service.*;
import com.example.eatgoodliveproject.utils.UniquePaymentIdGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private  final OrderItemRepository orderItemRepository;

    private final CartRepository cartRepository;
    private final CartService cartService;

    private final ProductService productService;
    private final PaystackPaymentRepository paymentRepository;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, OrderItemRepository orderItemRepository, CartRepository cartRepository, CartService cartService, ProductService productService,  PaystackPaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartService = cartService;
        this.paymentRepository = paymentRepository;
        this.productService = productService;
    }

    @Transactional
    @Override
    public OrderDto placeOrder(Long cartId, Long userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));


            Orders order = new Orders();
            order.setUser(user);
            order.setOrderDate(new Date());
            order.setTotalPrice(cart.getTotalPrice().add(cart.getTotalPrice().multiply(BigDecimal.valueOf(0.12))));
            order.setShippingMethod(ShippingMethod.AIR);
            order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
            order.setOrderVerification("Order" + UniquePaymentIdGenerator.generateId());
            order.setPaymentSuccessful(false);
            order.setReceived(true);
            Orders savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItems.add(orderItem);
        }
        orderItems = orderItemRepository.saveAll(orderItems);

            cartService.clearCart(cartId);
            OrderDto orderDto = new ObjectMapper().convertValue(savedOrder, OrderDto.class);
            orderItems.forEach(item -> orderDto.getOrderItems().add(new ObjectMapper().convertValue(item, OrderItemDto.class)));
            orderDto.setUser(user);
            orderDto.setTotalPrice(order.getTotalPrice());
            orderDto.setShippingMethod(order.getShippingMethod());
            orderDto.setReceived(savedOrder.isReceived());
            orderDto.setPrepared(savedOrder.isPrepared());
            orderDto.setReady(savedOrder.isReady());
            orderDto.setInTransit(savedOrder.isInTransit());
            orderDto.setDelivered(savedOrder.isDelivered());

            return orderDto;
    }

    @Transactional
    @Override
    public OrderResponseDto verifyPaymentAndConfirmOrder(String paymentReference, Long orderId) throws RuntimeException {
        // Retrieve the payment reference
        PaymentPaystack paymentPaystack = paymentRepository.findByReference(paymentReference)
                .orElseThrow(() -> new RuntimeException("Payment Reference cannot be retrieved " + paymentReference));


        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order has never been initiated"));

        // Check if the booking is already confirmed
        if (OrderStatus.PAYMENT_CONFIRMED.equals(order.getOrderStatus())) {
            throw new RuntimeException("Order is already confirmed");
        }

        if (paymentPaystack.getPaymentVerification() == null &&
                !paymentPaystack.getGatewayResponse().equalsIgnoreCase("successful")){
            throw new RuntimeException("Invalid Payment Verification");
        }



        // Update order status to PAYMENT_CONFIRMED
        order.setPaymentSuccessful(true);
        order.setOrderStatus(OrderStatus.PAYMENT_CONFIRMED);
        orderRepository.save(order);


        return OrderResponseDto.builder()
                .orderId(order.getId())
                .orderListSize(order.getOrderItems().size())
                .paymentMethod(paymentPaystack.getChannel())
                .productNames(order.getOrderItems()
                        .stream().map((item) ->(item.getProduct().getName()))
                        .collect(Collectors.toList()))
                .orderItemPrices(order.getOrderItems()
                        .stream()
                        .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .collect(Collectors.toList()))
                .totalPrice(order.getOrderItems()
                        .stream()
                        .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .tax(order.getTotalPrice().subtract(order.getOrderItems()
                        .stream()
                        .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)))
                .grandTotal(order.getTotalPrice())
                .build();
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId) {


        List<Orders> orders = orderRepository.findAllByUserId(userId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orders.get(0).getId());
        List<OrderDto> orderDtos = new ArrayList<>();


        for (Orders order : orders) {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setUser(order.getUser());
            orderItems.forEach(item -> orderDto.getOrderItems().add(new ObjectMapper().convertValue(item, OrderItemDto.class)));
            orderDto.setShippingMethod(order.getShippingMethod());
            orderDto.setTotalPrice(order.getTotalPrice());
            orderDto.setOrderDate(order.getOrderDate());
            orderDto.setReceived(order.isReceived());
            orderDto.setPrepared(order.isPrepared());
            orderDto.setReady(order.isReady());
            orderDto.setInTransit(order.isInTransit());
            orderDto.setDelivered(order.isDelivered());
            orderDtos.add(orderDto);
        }



        if (orderDtos.size() == 0) {
            throw new ResourceNotFoundException("No orders placed yet by the user with email: " + userId);
        }

        return orderDtos;
    }

    @Override
    public OrderDto getOrder(Long  userId, Long orderId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " not found"));
        Orders order = orderRepository.findByIdAndUserId(userId, orderId);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID " + orderId + " not found");
        }
        OrderDto orderDto = new OrderDto();
        orderDto.setId(orderId);
        orderDto.setUser(order.getUser());
        orderItems.forEach(item -> orderDto.getOrderItems().add(new ObjectMapper().convertValue(item, OrderItemDto.class)));
        orderDto.setShippingMethod(order.getShippingMethod());
        orderDto.setTotalPrice(order.getTotalPrice());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setReceived(order.isReceived());
        orderDto.setPrepared(order.isPrepared());
        orderDto.setReady(order.isReady());
        orderDto.setInTransit(order.isInTransit());
        orderDto.setDelivered(order.isDelivered());

        return orderDto;
    }

    @Override
    public OrderResponse getAllOrders (Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Orders> pageOrders = orderRepository.findAll(pageDetails);
        List<Orders> orders = pageOrders.getContent();
        List<OrderDto> orderDTOs = orders.stream().map(order -> new ObjectMapper().convertValue(order, OrderDto.class)).collect(Collectors.toList());

        if (orderDTOs.size() == 0) {
            throw new ResourceNotFoundException("No orders placed yet by the user ");
        }

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setLastPage(pageOrders.isLast());

        return orderResponse;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Orders> ordersList = orderRepository.findAll();
        List<OrderDto> orderDtoList = new ArrayList<>();
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(ordersList.get(0).getId());

        for (Orders order : ordersList) {
            OrderDto orderDto = new OrderDto();
            orderDto.setId(order.getId());
            orderDto.setUser(order.getUser());
            orderItems.forEach(item -> orderDto.getOrderItems().add(new ObjectMapper().convertValue(item, OrderItemDto.class)));
            orderDto.setShippingMethod(order.getShippingMethod());
            orderDto.setTotalPrice(order.getTotalPrice());
            orderDto.setOrderDate(order.getOrderDate());
            orderDto.setReceived(order.isReceived());
            orderDto.setPrepared(order.isPrepared());
            orderDto.setReady(order.isReady());
            orderDto.setInTransit(order.isInTransit());
            orderDto.setDelivered(order.isDelivered());
            orderDtoList.add(orderDto);
        }
        return orderDtoList;
    }


    @Override
    public ResponseEntity<TrackingDto> alertCustomers(Long userId, Long orderId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Orders order = orderRepository.findByIdAndUserId(userId, orderId);
        if (order == null) {
            throw new ResourceNotFoundException("Order with ID " + orderId + " not found");
        }
       TrackingDto trackingStatus = new TrackingDto();
        trackingStatus.setReceived(order.isReceived());
        trackingStatus.setPrepared(order.isPrepared());
        trackingStatus.setReady(order.isReady());
        trackingStatus.setInTransit(order.isInTransit());
        trackingStatus.setDelivered(order.isDelivered());
        return ResponseEntity.ok(trackingStatus);
    }

    @Override
    public ResponseEntity<?> acceptOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPrepared(true);
        orderRepository.save(order);
        return ResponseEntity.ok("Order Prepared");
    }

    @Override
    public ResponseEntity<String> orderReady(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));
        order.setReady(true);
        order.setInTransit(true);
        orderRepository.save(order);
        return ResponseEntity.ok("Order ready");
    }

    @Override
    public ResponseEntity<String> orderDelivered(Long orderId) {
        Orders order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found"));
        order.setDelivered(true);
        orderRepository.save(order);
        return ResponseEntity.ok("Order Delivered");
    }

}



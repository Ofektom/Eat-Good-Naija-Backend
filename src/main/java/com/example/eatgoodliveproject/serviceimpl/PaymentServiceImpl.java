//package com.example.eatgoodliveproject.serviceimpl;
//
//import com.example.eatgoodliveproject.enums.PaymentMethod;
//import com.example.eatgoodliveproject.exception.OrderNotFoundException;
//import com.example.eatgoodliveproject.model.Orders;
//import com.example.eatgoodliveproject.model.Payment;
//import com.example.eatgoodliveproject.repositories.OrderRepository;
//import com.example.eatgoodliveproject.repositories.PaymentRepository;
//import com.example.eatgoodliveproject.service.PaymentService;
//import org.springframework.stereotype.Service;
//
//@Service
//public class PaymentServiceImpl implements PaymentService {
//    private final PaymentRepository paymentRepository;
//    private final OrderRepository orderRepository;
//
//    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {
//        this.paymentRepository = paymentRepository;
//        this.orderRepository = orderRepository;
//    }
//
//    @Override
//    public Payment processPayment(Long orderId, PaymentMethod paymentMethod) {
//        Orders order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
//        Payment payment = new Payment();
//        payment.setOrder(order);
//        payment.setPaymentMethod(paymentMethod);
//        return paymentRepository.save(payment);
//
//    }
//}

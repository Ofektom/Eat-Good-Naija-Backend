package com.example.eatgoodliveproject.serviceimpl;

import com.example.eatgoodliveproject.dto.ReviewDto;
import com.example.eatgoodliveproject.exception.ResourceNotFoundException;
import com.example.eatgoodliveproject.model.Orders;
import com.example.eatgoodliveproject.model.Product;
import com.example.eatgoodliveproject.model.Review;
import com.example.eatgoodliveproject.repositories.OrderRepository;
import com.example.eatgoodliveproject.repositories.ProductRepository;
import com.example.eatgoodliveproject.repositories.ReviewRepository;
import com.example.eatgoodliveproject.repositories.UserRepository;
import com.example.eatgoodliveproject.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;


    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    @Override
    public void addReview(Long orderId, ReviewDto reviewDto) {
        Orders order = orderRepository.findById((orderId)).orElseThrow(() -> new RuntimeException("Order with ID" + orderId + " is not present"));
        Review review = new ObjectMapper().convertValue(reviewDto, Review.class);
        if(order != null) {
            review.setOrder(order);
            reviewRepository.save(review);
        }
    }

    @Override
    public ResponseEntity<Review> viewReview(Long reviewId) {
        return new ResponseEntity<>(reviewRepository.findById(reviewId).orElseThrow(() -> new ResourceNotFoundException("Product with ID "+ reviewId + " not found")), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> editReview(Long reviewId, ReviewDto reviewDto) {
        Review oldReview = reviewRepository.findById(reviewId).orElseThrow(() -> new RuntimeException("Review not found"));
        oldReview.setComplaint(reviewDto.getComplaint());
        oldReview.setRatings(reviewDto.getRatings());
        reviewRepository.save(oldReview);
        return new ResponseEntity<>("Review edited successfully", HttpStatus.OK);
    }

}

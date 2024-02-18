package com.example.eatgoodliveproject.controller;

import com.example.eatgoodliveproject.dto.ReviewDto;
import com.example.eatgoodliveproject.model.Review;
import com.example.eatgoodliveproject.service.ProductService;
import com.example.eatgoodliveproject.service.ReviewService;
import com.example.eatgoodliveproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final ProductService productService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService, ProductService productService) {
        this.reviewService = reviewService;
        this.userService = userService;
        this.productService = productService;
    }

    @PostMapping("/add/{orderId}")
    public void addReview(@PathVariable Long orderId, @RequestBody ReviewDto reviewDto){
        reviewService.addReview(orderId, reviewDto);
    }
    @GetMapping("/view/{reviewId}")
    public ResponseEntity<Review> viewProductReview(@PathVariable Long reviewId){
        return reviewService.viewReview(reviewId);
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<String> editReview(@PathVariable Long reviewId, @RequestBody ReviewDto reviewDto){
        return reviewService.editReview(reviewId, reviewDto);
    }
}

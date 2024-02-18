package com.example.eatgoodliveproject.service;

import com.example.eatgoodliveproject.dto.ReviewDto;
import com.example.eatgoodliveproject.model.Review;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    void addReview(Long orderId, ReviewDto reviewDto);

    ResponseEntity<Review> viewReview(Long reviewId);

    ResponseEntity<String> editReview(Long reviewId, ReviewDto reviewDto);
}

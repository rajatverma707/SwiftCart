package com.rv.review.controller;

import com.rv.review.dto.ReviewRequestDto;
import com.rv.review.dto.ReviewResponseDto;
import com.rv.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/create")
    public ResponseEntity<ReviewResponseDto> create(@RequestBody ReviewRequestDto requestDto) {
        ReviewResponseDto created = reviewService.createReview(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/product/{productId}")
    public List<ReviewResponseDto> getByProduct(@PathVariable Long productId) {
        return reviewService.getReviewsForProduct(productId);
    }

    @GetMapping("/customer/{email}")
    public List<ReviewResponseDto> getByCustomer(@PathVariable("email") String customerEmail) {
        return reviewService.getReviewsForCustomer(customerEmail);
    }
}

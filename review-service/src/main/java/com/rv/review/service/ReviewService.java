package com.rv.review.service;

import com.rv.review.dto.ReviewRequestDto;
import com.rv.review.dto.ReviewResponseDto;

import java.util.List;

public interface ReviewService {

    ReviewResponseDto createReview(ReviewRequestDto requestDto);

    List<ReviewResponseDto> getReviewsForProduct(Long productId);

    List<ReviewResponseDto> getReviewsForCustomer(String customerEmail);
}

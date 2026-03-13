package com.rv.review.service;

import com.rv.review.dto.OrderApiResponse;
import com.rv.review.dto.OrderSummaryDto;
import com.rv.review.dto.ReviewRequestDto;
import com.rv.review.dto.ReviewResponseDto;
import com.rv.review.entity.Review;
import com.rv.review.exception.ReviewNotAllowedException;
import com.rv.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final WebClient webClient;

    @Value("${order.service.url:http://localhost:8082}")
    private String orderServiceUrl;

    @Override
    public ReviewResponseDto createReview(ReviewRequestDto requestDto) {

        boolean alreadyReviewed = reviewRepository.existsByCustomerEmailAndProductId(
            requestDto.getCustomerEmail(),
            requestDto.getProductId()
        );
        if (alreadyReviewed) {
            throw new ReviewNotAllowedException("Review already submitted for this order and product");
        }

        OrderApiResponse<List<OrderSummaryDto>> orderResponse = webClient.get()
            .uri(orderServiceUrl + "/api/v1/orders/customer-orders/" + requestDto.getCustomerEmail())
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<OrderApiResponse<List<OrderSummaryDto>>>() {})
            .block();

        boolean hasMatchingCompletedOrder = orderResponse != null
                && orderResponse.getData() != null
                && orderResponse.getData().stream()
                .anyMatch(o -> requestDto.getOrderTrackingNum().equals(o.getOrderTrackingNum())
                        && "COMPLETED".equalsIgnoreCase(o.getOrderStatus()));

        if (!hasMatchingCompletedOrder) {
            throw new ReviewNotAllowedException("No COMPLETED order found for this customer and tracking number, review not allowed");
        }

        Review review = new Review();
        review.setProductId(requestDto.getProductId());
        review.setCustomerEmail(requestDto.getCustomerEmail());
        review.setOrderTrackingNum(requestDto.getOrderTrackingNum());
        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());
        review.setCreatedAt(LocalDateTime.now());

        Review saved = reviewRepository.save(review);
        return mapToDto(saved);
    }

    @Override
    public List<ReviewResponseDto> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDto> getReviewsForCustomer(String customerEmail) {
        return reviewRepository.findByCustomerEmail(customerEmail)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ReviewResponseDto mapToDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setProductId(review.getProductId());
        dto.setCustomerEmail(review.getCustomerEmail());
        dto.setOrderTrackingNum(review.getOrderTrackingNum());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}

package com.rv.review.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {

    private Long id;
    private Long productId;
    private String customerEmail;
    private String orderTrackingNum;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}

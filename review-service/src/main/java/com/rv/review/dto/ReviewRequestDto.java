package com.rv.review.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {

    private Long productId;
    private String customerEmail;
    private String orderTrackingNum;
    private Integer rating;
    private String comment;
}

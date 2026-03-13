package com.rv.review.dto;

import lombok.Data;

@Data
public class OrderApiResponse<T> {

    private Integer status;
    private String msg;
    private T data;
}

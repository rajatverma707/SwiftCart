package com.rv.admin.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private Integer statusCode;
    private String message;
    private T data;
}

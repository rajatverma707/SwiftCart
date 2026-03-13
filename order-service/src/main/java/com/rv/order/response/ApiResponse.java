package com.rv.order.response;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private Integer status;
    private String msg;
    private T data;
}


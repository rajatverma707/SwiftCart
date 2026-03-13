package com.rv.admin.client;

import java.util.List;

import lombok.Data;

@Data
public class OrderListResponse {

    private Integer status;
    private String msg;
    private List<OrderSummary> data;
}

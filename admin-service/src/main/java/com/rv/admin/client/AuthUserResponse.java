package com.rv.admin.client;

import lombok.Data;

@Data
public class AuthUserResponse {

    private Integer statusCode;
    private String message;
    private UserSummary data;
}

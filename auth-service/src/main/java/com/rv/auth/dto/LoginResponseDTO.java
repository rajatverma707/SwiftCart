package com.rv.auth.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String message;
    private String token;
    private String bearerType;
    private long expiresIn;

    public LoginResponseDTO() {}

    public LoginResponseDTO(String message, String token, String bearerType, long expiresIn) {
        this.message = message;
        this.token = token;
        this.bearerType = bearerType;
        this.expiresIn = expiresIn;
    }
}

package com.rv.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email
) {}


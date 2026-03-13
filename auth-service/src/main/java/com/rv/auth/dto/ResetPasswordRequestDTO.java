package com.rv.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(
    @NotBlank(message = "Token is required")
    String token,

    @NotBlank(message = "New password is required")
    String newPassword
) {}

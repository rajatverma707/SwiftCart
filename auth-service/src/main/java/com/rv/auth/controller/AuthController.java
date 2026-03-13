package com.rv.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rv.auth.dto.ForgotPasswordRequestDTO;
import com.rv.auth.dto.LoginRequestDTO;
import com.rv.auth.dto.LoginResponseDTO;
import com.rv.auth.dto.RegisterRequestDTO;
import com.rv.auth.dto.ResetPasswordRequestDTO;
import com.rv.auth.dto.UserResponseDTO;
import com.rv.auth.service.AuthService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
            UserResponseDTO response = authService.register(request);
            return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@Valid @RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.ok("Password reset link sent");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok("Password reset successful");
    }
}

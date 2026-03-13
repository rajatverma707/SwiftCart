package com.rv.auth.service;

import com.rv.auth.dto.RegisterRequestDTO;
import com.rv.auth.entity.RoleEntity;
import com.rv.auth.entity.UserEntity;
import com.rv.auth.repository.RoleRepository;
import com.rv.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegister_UserAlreadyExists() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setMobileNum("1234567890");
        when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.of(new UserEntity()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertEquals("User already exists with this email", exception.getMessage());

        verify(userRepo, never()).save(any(UserEntity.class));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testRegister_Success() {
        RegisterRequestDTO request = new RegisterRequestDTO();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setMobileNum("1234567890");
        RoleEntity role = new RoleEntity();
        role.setRoleId(1);

        when(userRepo.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleRepo.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        authService.register(request);

        verify(userRepo, times(1)).save(any(UserEntity.class));
        verify(emailService, times(1)).sendEmail(eq(request.getEmail()), eq("Registration Successful"), anyString());
    }
}

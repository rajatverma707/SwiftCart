package com.rv.auth.service;

import com.rv.auth.config.JwtService;
import com.rv.auth.dto.*;
import com.rv.auth.entity.RoleEntity;
import com.rv.auth.entity.UserEntity;
import com.rv.auth.mapper.UserMapper;
import com.rv.auth.repository.RoleRepository;
import com.rv.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepo,
                       RoleRepository roleRepo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       EmailService emailService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
		this.emailService = emailService;
    }

    // ✅ REGISTER
    public UserResponseDTO register(RegisterRequestDTO request) {

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists with this email");
        }

        RoleEntity role = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role not found"));

        UserEntity user = new UserEntity();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(Long.parseLong(request.getMobileNum()));
        user.getRoles().add(role);

        userRepo.save(user);

        String registrationEmail = "Dear " + user.getName() + ",\n\n" +
                "Welcome to our SwiftMart Platform!\n\n" +
                "Your registration has been successfully completed. You can now log in to your account using the credentials you provided.\n\n" +
                "Email: " + user.getEmail() + "\n\n" +
                "You can now access all the features and services available on our platform.\n\n" +
                "For your security, we recommend that you change your password immediately upon your first login.\n\n" +
                "If you have any questions or need assistance, please don't hesitate to contact our support team at support@swiftmart.com\n\n" +
                "Best regards,\n" +
                "SwiftMart Team\n\n" +
                "Note: This is an automated message. Please do not reply to this email.";
        
        emailService.sendEmail(user.getEmail(), "Registration Successful - Welcome to SwiftMart Platform", registrationEmail);

        return UserMapper.toUserResponseDto(user);
    }

    // ✅ LOGIN
    public LoginResponseDTO login(LoginRequestDTO request) {
    	LoginResponseDTO loginResponseDTO= null;
        UserEntity user = userRepo.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {

            loginResponseDTO = new LoginResponseDTO("Invalid credentials","","",0);
        } else {
		  String token = jwtService.generateToken(user);
          loginResponseDTO =  new LoginResponseDTO(
			"Logged in successfully",
                  token,
                  "Bearer",
                  900
          );
        }
      

        return loginResponseDTO;
    }
    
    // ================= FORGOT PASSWORD =================
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        UserEntity user = userRepo.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String resetToken = jwtService.generatePasswordResetToken(user.getEmail());

        String resetLink =
                "http://localhost:8080/auth/reset-password?token=" + resetToken;

        emailService.sendEmail(
                user.getEmail(),
                "Reset Password",
                "Click here to reset your password:\n" + resetLink
        );
    }

    // ================= RESET PASSWORD =================
    public void resetPassword(ResetPasswordRequestDTO dto) {
        jwtService.validateResetToken(dto.token());

        String email = jwtService.extractEmail(dto.token());

        UserEntity user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepo.save(user);
    }
}

package com.rv.user.controller;

import com.rv.user.util.JwtUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
  
import com.rv.user.dto.ApiResponse;
import com.rv.user.dto.LoginRequestDTO;
import com.rv.user.dto.LoginResponseDTO;
import com.rv.user.dto.ResetPwdDto;
import com.rv.user.dto.ShippingAddressDto;
import com.rv.user.dto.UserDto;
import com.rv.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserDto userDto) {
        UserDto savedUser = userService.saveUser(userDto);
        ApiResponse<UserDto> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("User created successfully");
        response.setData(savedUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtUtil jwtUtil;

@PostMapping("/login")
public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody LoginRequestDTO loginRequestDTO) {
    UserDetails userDetails;
    ApiResponse<LoginResponseDTO> response = new ApiResponse<>();
    try {
        userDetails = userDetailsService.loadUserByUsername(loginRequestDTO.email());
    } catch (Exception e) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Invalid credentials");
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    if (!new BCryptPasswordEncoder().matches(loginRequestDTO.password(), userDetails.getPassword())) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        response.setMessage("Invalid credentials");
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    var roles = userDetails.getAuthorities().stream().map(a -> a.getAuthority()).toList();
    String token = jwtUtil.generateToken(userDetails.getUsername(), roles);
    LoginResponseDTO loginResponse = new LoginResponseDTO(userDetails.getUsername(), token, roles.isEmpty() ? null : roles.get(0));
    response.setStatusCode(HttpStatus.OK.value());
    response.setMessage("Login successful");
    response.setData(loginResponse);
    return new ResponseEntity<>(response, HttpStatus.OK);
}

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody ResetPwdDto resetPwdDto) {
        ResetPwdDto result = userService.resetPassword(resetPwdDto);
        ApiResponse<String> response = new ApiResponse<>();
        if (result == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Password reset failed. Please check your input.");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Password reset successfully");
        response.setData(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/email")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<UserDto>> getUserByEmail(@RequestParam String email) {
        UserDto userDto = userService.getUserByEmail(email);
        ApiResponse<UserDto> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("User retrieved successfully");
        response.setData(userDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

         @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    @PostMapping("/{userId}/shipping-address")
    public ResponseEntity<ApiResponse<ShippingAddressDto>> saveShippingAddress(@PathVariable Integer userId, @RequestBody ShippingAddressDto shippingAddressDto) {
        ShippingAddressDto savedAddress = userService.saveShippingAddress(userId, shippingAddressDto);
        ApiResponse<ShippingAddressDto> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.CREATED.value());
        response.setMessage("Shipping address saved successfully");
        response.setData(savedAddress);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_CUSTOMER') or hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/shipping-address/{shippingAddressId}")
    public ResponseEntity<ApiResponse<ShippingAddressDto>> deleteShippingAddress(@PathVariable Integer shippingAddressId) {
        ShippingAddressDto deletedAddress = userService.deleteShippingAddress(shippingAddressId);
        ApiResponse<ShippingAddressDto> response = new ApiResponse<>();
        response.setStatusCode(HttpStatus.OK.value());
        response.setMessage("Shipping address deleted successfully");
        response.setData(deletedAddress);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

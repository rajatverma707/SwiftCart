package com.rv.auth.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.rv.auth.entity.UserEntity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Value("${jwt.expiry-minutes}")
    private long expiryMinutes;

    @Value("${jwt.reset-expiry-minutes}")
    private long resetExpiryMinutes;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

        // 🔐 ACCESS TOKEN
        public String generateToken(UserEntity user) {
        Instant now = Instant.now();

        // Collect role names, e.g. ["ROLE_USER", "ROLE_ADMIN"]
        Set<String> roleNames = user.getRoles().stream()
            .map(r -> r.getName())
            .collect(Collectors.toSet());

        JwtClaimsSet claims = JwtClaimsSet.builder()
            // keep subject as the username/email
            .subject(user.getEmail())
            .issuedAt(now)
            .expiresAt(now.plus(expiryMinutes, ChronoUnit.MINUTES))
            .claim("type", "ACCESS")
            // custom claims used by gateway and downstream services
            .claim("userId", String.valueOf(user.getUserId()))
            .claim("roles", roleNames)
            .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
        }

    public String extractUsername(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        Jwt jwt = jwtDecoder.decode(token);

        return jwt.getSubject().equals(userDetails.getUsername())
                && jwt.getExpiresAt().isAfter(Instant.now());
    }

    // 🔑 PASSWORD RESET TOKEN
    public String generatePasswordResetToken(String email) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(email)
                .issuedAt(now)
                .expiresAt(now.plus(resetExpiryMinutes, ChronoUnit.MINUTES))
                .claim("type", "PASSWORD_RESET")
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder
            .encode(JwtEncoderParameters.from(jwsHeader, claims))
            .getTokenValue();
    }

    public String extractEmail(String token) {
        return jwtDecoder.decode(token).getSubject();
    }

    public void validateResetToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);

        if (jwt.getExpiresAt().isBefore(Instant.now())) {
            throw new RuntimeException("Reset token expired");
        }
    }
}





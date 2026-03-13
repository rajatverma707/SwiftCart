package com.retail.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        List<String> publicEndpoints = List.of(
                "/api/user/login",
                "/api/user/create"
        );
        // Allow public endpoints without JWT validation
        if (publicEndpoints.contains(path)) {
            return chain.filter(exchange);
        }
        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return this.onError(exchange, "Missing or invalid Authorization header", HttpStatus.UNAUTHORIZED);
        }
        String token = authHeader.substring(7);
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            // Optionally, you can add claims to headers for downstream services
                Object rolesObj = claims.get("roles");
                String rolesHeader = "";
                if (rolesObj instanceof List<?> rolesList) {
                rolesHeader = String.join(",", rolesList.stream().map(Object::toString).toList());
                }
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Name", claims.getSubject())
                    .header("X-User-Roles", rolesHeader)
                    .build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            return this.onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

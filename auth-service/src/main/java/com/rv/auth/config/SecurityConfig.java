package com.rv.auth.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.rv.auth.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret-key}")
    private String secretKey;

	    private final JwtAuthFilter jwtAuthFilter;
	    private final UserDetailsServiceImpl userDetailsService;

	    public SecurityConfig(JwtAuthFilter jwtAuthFilter,
	                          UserDetailsServiceImpl userDetailsService) {
	        this.jwtAuthFilter = jwtAuthFilter;
	        this.userDetailsService = userDetailsService;
	    }

	    @Bean
	    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	        http
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(sm ->
	                sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
        			    "/auth/login",
        			    "/auth/register",
        			    "/auth/forgot-password",
        			    "/auth/reset-password",
						"/api/user/login"
        			).permitAll()
	                .requestMatchers("/dashboard").authenticated()
	                .anyRequest().authenticated()
	            )
	            .addFilterBefore(jwtAuthFilter,
	                UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	    }



@SuppressWarnings("deprecation")
	@Bean
	    public AuthenticationProvider authenticationProvider() {
	        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
	        provider.setUserDetailsService(userDetailsService);
	        provider.setPasswordEncoder(passwordEncoder());
	        return provider;
	    }

	    @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }
	    
	}



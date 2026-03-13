package com.rv.auth.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rv.auth.entity.UserEntity;
import com.rv.auth.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	    private final UserRepository userRepository;

	    public UserDetailsServiceImpl(UserRepository userRepo) {
	        this.userRepository = userRepo;
	    }

	    @Override
	    public UserDetails loadUserByUsername(String email) {

	        UserEntity user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

			List<SimpleGrantedAuthority> authorities = user.getRoles()
				.stream()
				.map(r -> new SimpleGrantedAuthority(r.getRoleId().toString()))
				.toList();

	        return new org.springframework.security.core.userdetails.User(
	                user.getEmail(),
	                user.getPassword(),
	                authorities
	        );
	    }
	}



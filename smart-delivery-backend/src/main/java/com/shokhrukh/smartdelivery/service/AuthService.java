package com.shokhrukh.smartdelivery.service;

import com.shokhrukh.smartdelivery.dto.*;
import com.shokhrukh.smartdelivery.entity.User;
import com.shokhrukh.smartdelivery.enums.Role;
import com.shokhrukh.smartdelivery.entity.User;
import com.shokhrukh.smartdelivery.repository.UserRepository;
import com.shokhrukh.smartdelivery.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (request.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("ADMIN registration is not allowed");
        }

        var user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setName(request.getName());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getRole()); // ✅ token is String

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user.getEmail(), user.getRole()); // ✅ token is String

        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole());
    }

    public UserResponse me(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRole());
    }
}
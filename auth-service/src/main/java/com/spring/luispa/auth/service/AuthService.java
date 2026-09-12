package com.spring.luispa.auth.service;

import com.spring.luispa.auth.domain.Role;
import com.spring.luispa.auth.domain.User;
import com.spring.luispa.auth.dto.AuthResponse;
import com.spring.luispa.auth.dto.LoginRequest;
import com.spring.luispa.auth.dto.RegisterRequest;
import com.spring.luispa.auth.repository.UserRepository;
import com.spring.luispa.auth.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(Role.USER)
                .enabled(true)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: id={}, email={}", user.getId(), user.getEmail());

        String token = jwtUtils.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(
                token,
                "Bearer",
                jwtUtils.getExpirationMs(),
                user.getId().toString(),
                user.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt: {}", request.email());

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        String token = jwtUtils.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getRole().name()
        );

        log.info("Login successful: userId={}", user.getId());

        return new AuthResponse(
                token,
                "Bearer",
                jwtUtils.getExpirationMs(),
                user.getId().toString(),
                user.getEmail()
        );
    }
}

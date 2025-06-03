// --- AuthService.java ---
package com.flowly.auth.service;

import com.flowly.auth.dto.*;
import com.flowly.shared.model.Tenant;
import com.flowly.shared.model.User;
import com.flowly.shared.repository.TenantRepository;
import com.flowly.shared.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        Tenant tenant = tenantRepository.findById(request.getTenantId())
            .orElseThrow(() -> new EntityNotFoundException("Tenant not found"));

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role("USER")
            .tenant(tenant)   // <-- Set the managed Tenant entity here
            .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getRole(),token);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getRole(),token);
    }
}
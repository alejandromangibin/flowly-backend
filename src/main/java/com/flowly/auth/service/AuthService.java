// --- AuthService.java ---
package com.flowly.auth.service;

import com.flowly.auth.dto.*;
import com.flowly.shared.model.User;
import com.flowly.shared.repository.UserRepository;
import com.flowly.tenant.service.TenantEntityService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TenantEntityService tenantEntityService;

    public boolean isSubdomainAvailable(String subdomain) {
        return tenantEntityService.isSubdomainAvailable(subdomain);
    }

    public AuthResponse register(RegisterRequest request) {
        
        if (request.getUsername() == null || request.getEmail() == null || request.getPassword() == null || request.getRole() == null) {
            throw new IllegalArgumentException("Username, email, role, and password are required");
        }
        
        if (request.getUsername().isEmpty() || request.getEmail().isEmpty() || request.getPassword().isEmpty() || request.getRole().isEmpty()) {
            throw new IllegalArgumentException("Username, email, role, and password cannot be empty");
        }

        User user = User.builder()
            .username(request.getUsername())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(request.getRole())
            .build();
        

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
         
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

    public JwtService getJwtService() {
        return jwtService;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
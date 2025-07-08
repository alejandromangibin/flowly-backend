// --- AuthController.java ---
package com.flowly.auth.controller;

import com.flowly.auth.dto.*;
import com.flowly.auth.service.AuthService;
import com.flowly.exception.InvalidSubdomainException;
import com.flowly.shared.model.User;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request, @RequestHeader("X-Tenant-ID") String subdomain) {
        // checkSubdomain(subdomain, false);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request, @RequestHeader("X-Tenant-ID") String subdomain) {
        checkSubdomain(subdomain, true);
        System.out.println("Login attempt: " + request.getEmail());
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@RequestHeader("Authorization") String authorizationHeader) {


        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Missing or invalid Authorization header"));
        }
        String token = authorizationHeader.substring(7);
        try {
            User user = getUserFromToken(token);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Invalid token"));
            }
            return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "email", user.getEmail(),
                "role", user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid or expired token"));
        }
    }

    private User getUserFromToken(String token) {
        String email = authService.getJwtService().extractUsername(token);
        if (email == null) return null;
        return authService.getUserByEmail(email);
    }

    protected void checkSubdomain(String subdomain, boolean isLogin) {
        System.out.println("Checking subdomain: " + subdomain);

        if (subdomain == null || subdomain.isBlank()) {
            throw new InvalidSubdomainException("Subdomain cannot be null or blank.");
        }

        if (isLogin && authService.isSubdomainAvailable(subdomain)) {
            throw new InvalidSubdomainException("Invalid Subdomain.");
        }

        if (!isLogin && !authService.isSubdomainAvailable(subdomain)) {
            throw new InvalidSubdomainException("Subdomain is invalid or already taken.");
        }
    }

}
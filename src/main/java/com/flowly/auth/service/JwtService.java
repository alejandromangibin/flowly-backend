// --- JwtService.java ---
package com.flowly.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.flowly.config.FlowlySecurityProperties;
import com.flowly.shared.model.User;

import java.security.Key;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final long EXPIRATION = 86400000; // 1 day
    private final FlowlySecurityProperties securityProperties;
    
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole())
                .claim("tenantId", user.getTenant().getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getKey())
                .compact();
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(
            // SECRET
            securityProperties.jwt().getSecret()
            .getBytes());
    }

    public String extractUsername(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }


}
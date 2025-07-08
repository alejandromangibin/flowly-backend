package com.flowly.auth.filter;

import com.flowly.auth.service.CustomUserDetailsService;
import com.flowly.auth.service.JwtService;
import com.flowly.config.multitenancy.TenantContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @SuppressWarnings("null")
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt;
            final String userEmail;

            boolean isAuthenticatedRequest = authHeader != null && authHeader.startsWith("Bearer ");

            if (isAuthenticatedRequest) {

                jwt = authHeader.substring(7);
                
                if (jwt.isBlank()) {
                    throw new RuntimeException("JWT is empty");
                }

                String tenantFromJwt = jwtService.extractTenantSchema(jwt);
                
                if (tenantFromJwt == null || tenantFromJwt.isBlank()) {
                    throw new RuntimeException("Missing tenant schema in JWT");
                }
                
                System.out.println("Setting tenant context from JWT: " + tenantFromJwt);
                TenantContextHolder.setCurrentTenant(tenantFromJwt);

                userEmail = jwtService.extractUsername(jwt);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        // Extract tenant from JWT
                        String tenantSchema = jwtService.extractTenantSchema(jwt);
                        if (tenantSchema == null || tenantSchema.isBlank()) {
                            throw new RuntimeException("Missing tenant schema in JWT");
                        }

                        System.out.println("Setting tenant context from JWT: " + tenantSchema);
                        TenantContextHolder.setCurrentTenant(tenantSchema);

                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Authentication set in SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());
                    }
                }
            } else {
                // Unauthenticated or public route (e.g. tenant registration)
                String tenantFromHeader = request.getHeader(TENANT_HEADER);
                if (tenantFromHeader != null && !tenantFromHeader.isBlank()) {
                    System.out.println("Setting tenant context from X-Tenant-ID header: " + tenantFromHeader);
                    TenantContextHolder.setCurrentTenant(tenantFromHeader);
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContextHolder.clear(); // always clean up
        }
    }






    // @Override
    // protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    //     String path = request.getRequestURI();

    //     return path.startsWith("/") ||
    //         path.startsWith("/vaadin") ||
    //         path.startsWith("/frontend") ||
    //         path.startsWith("/VAADIN") ||
    //         path.startsWith("/icons") ||
    //         path.startsWith("/images") ||
    //         path.startsWith("/styles") ||
    //         path.startsWith("/webjars") ||
    //         path.startsWith("/manifest.webmanifest") ||
    //         path.startsWith("/sw.js") ||
    //         path.startsWith("/favicon.ico") ||
    //         path.startsWith("/login") || 
    //         path.startsWith("/api/auth"); // adjust based on your public API routes
    // }

}

package com.flowly;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowly.config.multitenancy.TenantContextHolder;
import com.flowly.shared.model.User;
import com.flowly.shared.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/secure")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public String secure() {
        return "You accessed a secured endpoint!";
    }

    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createTestUser() {
        System.out.println("START public String createTestUser()");
        // Tenant context should already be set by JwtAuthenticationFilter
        String currentTenant = TenantContextHolder.getCurrentTenant();
        System.out.println("Current tenant from context: " + currentTenant);
        
        if (currentTenant == null || "public".equals(currentTenant)) {
            return "Error: No tenant context found. Make sure you're authenticated with a valid JWT token.";
        }

        createUser();
        System.out.println("END public String createTestUser()");
        return "User saved successfully in tenant schema: " + currentTenant;
    }


    @Transactional
    protected void createUser() {
        System.out.println("START protected void createUser()");
        // Create tenant-specific user (will be saved in the tenant's schema)
        User user = User.builder()
                .username("testuser_" + System.currentTimeMillis())
                .email("testuser" + System.currentTimeMillis() + "@example.com")
                .password("test1234")
                .role("USER")
                .firstName("Test")
                .lastName("User")
                .department("Engineering")
                .build();

        userRepository.save(user);
        System.out.println("END protected void createUser()");
    }


    @GetMapping("/context")
    @PreAuthorize("isAuthenticated()")
    public String getTenantContext() {
        String currentTenant = TenantContextHolder.getCurrentTenant();
        return "Current tenant context: " + currentTenant;
    }
}
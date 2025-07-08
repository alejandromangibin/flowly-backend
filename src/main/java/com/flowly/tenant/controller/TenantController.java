package com.flowly.tenant.controller;

import com.flowly.shared.model.Tenant;
import com.flowly.tenant.dto.TenantRegistrationRequest;
import com.flowly.tenant.service.TenantEntityService;
import com.flowly.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.flowly.exception.InvalidSubdomainException;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;
    private final TenantEntityService tenantEntityService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerTenant(@RequestBody TenantRegistrationRequest request) {
        UUID tenantId = UUID.randomUUID();

        String schemaName = request.getSubdomain()
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase();

        // Validate subdomain
        if (schemaName.isBlank() || !tenantEntityService.isSubdomainAvailable(schemaName)) {
            throw new InvalidSubdomainException("Subdomain is invalid or already taken.");
        }

        Tenant tenant = Tenant.builder()
            .id(tenantId)
            .name(request.getBusinessName())
            .email(request.getOwnerEmail())
            .schemaName(schemaName)
            .birTin(request.getBirTin())
            .subscriptionPlan(request.getSubscriptionPlan())
            .industry(request.getIndustry())
            .active(true)
            .build();

        tenantId = tenantService.registerTenant(request, tenant);

        Map<String, Object> response = Map.of("tenantId", tenantId.toString());
        
        return ResponseEntity.ok(response); // ✅ consistent JSON
    }

    @GetMapping("/check-subdomain")
    public ResponseEntity<Map<String, Object>> checkSubdomain(@RequestParam String subdomain) {
        Logger logger = Logger.getLogger(TenantController.class.getName());
        logger.info("Checking availability for subdomain: " + subdomain);
        if (subdomain == null || subdomain.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing subdomain"));
        }
        
        boolean available = tenantEntityService.isSubdomainAvailable(subdomain);
        return ResponseEntity.ok(Map.of("available", available));
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listTenants() {
        Logger logger = Logger.getLogger(TenantController.class.getName());
        logger.info("Listing all tenants");
        
        Map<String, Object> tenants = tenantEntityService.listTenants();
        return ResponseEntity.ok(tenants);
    }

}

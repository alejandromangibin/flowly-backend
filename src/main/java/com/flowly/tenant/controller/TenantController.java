package com.flowly.tenant.controller;

import com.flowly.tenant.dto.TenantRegistrationRequest;
import com.flowly.tenant.service.TenantService;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerTenant(@RequestBody TenantRegistrationRequest request) {
        UUID tenantId = tenantService.registerTenant(request);
        Map<String, Object> response = Map.of("tenantId", tenantId.toString());
        return ResponseEntity.ok(response); // ✅ consistent JSON
    }

}

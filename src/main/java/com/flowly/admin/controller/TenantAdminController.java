//TenantAdminController.java
package com.flowly.admin.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flowly.shared.model.Tenant;
import com.flowly.shared.repository.TenantRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/tenants")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TenantAdminController {

    private final TenantRepository tenantRepository;

    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

}

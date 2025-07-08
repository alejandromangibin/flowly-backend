package com.flowly.tenant.service;

import com.flowly.shared.model.Tenant;
import com.flowly.tenant.dto.TenantRegistrationRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
// import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final JdbcTemplate jdbcTemplate;
    private final TenantSchemaService tenantSchemaService;
    private final TenantEntityService tenantEntityService;

    public UUID registerTenant(TenantRegistrationRequest request, Tenant tenant) {
        

        tenantSchemaService.migrateSchema(tenant.getSchemaName(), jdbcTemplate);
        tenantEntityService.registerTenantEntities(request, tenant);


        return tenant.getId();

    }

}

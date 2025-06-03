package com.flowly.tenant.service;

import com.flowly.config.props.MultiTenancyProperties;
import com.flowly.shared.model.Tenant;
import com.flowly.shared.repository.TenantRepository;
import com.flowly.tenant.dto.TenantRegistrationRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final JdbcTemplate jdbcTemplate;
    private final MultiTenancyProperties multiTenancyProperties;

    @Transactional
    public UUID registerTenant(TenantRegistrationRequest request) {
        UUID tenantId = UUID.randomUUID();
        String schemaPrefix = multiTenancyProperties.schemaPrefix(); // or getSchemaPrefix() if not using record
        String schemaName = schemaPrefix + tenantId.toString().replace("-", "").substring(0, 8);

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS " + schemaName);

        Tenant tenant = Tenant.builder()
            .id(tenantId)
            .name(request.getBusinessName())
            .email(request.getOwnerEmail())
            .schemaName(schemaName)
            .build();

        tenantRepository.save(tenant);
        return tenantId;
    }
}


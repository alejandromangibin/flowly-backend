package com.flowly.tenant.service;

import com.flowly.shared.model.Tenant;
import com.flowly.shared.model.User;
import com.flowly.shared.repository.TenantRepository;
import com.flowly.shared.repository.UserRepository;
import com.flowly.tenant.dto.TenantRegistrationRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class TenantEntityService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UUID registerTenantEntities(
            TenantRegistrationRequest request,
            Tenant tenant
            ) {

        tenantRepository.saveAndFlush(tenant);

        User appUser = User.builder()
                .username(request.getOwnerEmail())
                .email(request.getOwnerEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ADMIN")
                // .tenantId(tenantId)
                .build();
        
        userRepository.save(appUser);


        return tenant.getId();
    }



    
    public boolean isSubdomainAvailable(String subdomain) {
        Logger logger = Logger.getLogger(TenantService.class.getName());
        logger.info("Checking availability for subdomain: " + subdomain);
        if (subdomain == null || subdomain.isBlank()) {
            return false; // Invalid input
        }
        return tenantRepository.findBySchemaName(subdomain).isEmpty();
    }




    public Map<String, Object> listTenants() {
        Logger logger = Logger.getLogger(TenantEntityService.class.getName());
        logger.info("Listing all tenants");
        
        var tenants = tenantRepository.findAll();
        return Map.of("tenants", tenants);
    }
}

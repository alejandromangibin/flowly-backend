// --- UserRepository.java ---
package com.flowly.shared.repository;

import com.flowly.shared.model.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
}

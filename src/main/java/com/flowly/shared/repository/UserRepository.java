// --- UserRepository.java ---
package com.flowly.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.flowly.shared.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsById(@NonNull UUID id);
    boolean existsByTenantId(UUID tenantId);
    Optional<User> findByTenantId(UUID tenantId);
    
}

// --- Tenant.java ---
package com.flowly.shared.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tenant", schema = "public")
public class Tenant {
    @Id    
    @Column(name = "tenant_id", nullable = false)
    // @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "schema_name", nullable = false, unique = true)
    private String schemaName;

    @Column(nullable = false)
    private String name;

    @Column
    private String subscriptionPlan;

    @Column
    private Boolean active;
    
    // Philippine-specific fields
    @Column
    private String birTin;

    @Column
    private String businessType; // corporation, partnership, sole_proprietorship

    private String industry;
    private String timezone;

    @Column(nullable = false)
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
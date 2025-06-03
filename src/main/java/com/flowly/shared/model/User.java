// --- User.java ---
package com.flowly.shared.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "app_user", schema = "public")
public class User {
   @Id
    @GeneratedValue
    //@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @UuidGenerator
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID id;

    private String username;
    private String email;
    private String password;
    private String role;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
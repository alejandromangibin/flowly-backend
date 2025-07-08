package com.flowly.shared.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "lead")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead {

    @Id
    @GeneratedValue
    @Column(name = "lead_id", updatable = false, nullable = false)
    private UUID leadId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadSource source;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public enum LeadSource {
        WEB, FORM, REFERRAL
    }

    public enum LeadStatus {
        NEW, CONTACTED, QUALIFIED
    }
}

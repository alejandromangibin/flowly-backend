// --- Organization.java ---
package com.flowly.tenant.business;

import com.flowly.shared.model.Tenant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @ManyToOne
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;
}
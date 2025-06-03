package com.flowly.tenant.dto;

import lombok.Data;

@Data
public class TenantRegistrationRequest {
    private String businessName;
    private String ownerEmail;
    private String birTin; // Philippine-specific
    private String businessType; // corporation, partnership, sole_proprietorship
    private String industry; // e.g., retail, services, manufacturing
}
